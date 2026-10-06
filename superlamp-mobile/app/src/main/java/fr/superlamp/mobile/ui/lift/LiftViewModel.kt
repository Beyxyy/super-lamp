package fr.superlamp.mobile.ui.lift

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.LiftSet
import fr.superlamp.mobile.data.entity.LiftSetDetail
import fr.superlamp.mobile.data.entity.WorkoutExerciseDetail
import fr.superlamp.mobile.data.repository.ExerciseRepository
import fr.superlamp.mobile.data.repository.LiftRepository
import fr.superlamp.mobile.data.repository.ProgramRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.appViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Un exercice de la séance : planifié (séance type) et/ou avec des séries enregistrées. */
data class ExerciseBlock(
    val exerciseId: Long,
    val name: String,
    val workoutExerciseId: Long?,
    val plannedSets: Int?,
    val plannedReps: Int?,
    val restTimeSeconds: Int?,
    val sets: List<LiftSet>,
    /** Séries faites sur cet exercice lors de la séance précédente. */
    val previous: List<LiftSet>,
)

data class LiftUiState(
    val lift: Lift? = null,
    val blocks: List<ExerciseBlock> = emptyList(),
    val loaded: Boolean = false,
) {
    val setCount: Int get() = blocks.sumOf { it.sets.size }
    val volume: Double get() = blocks.sumOf { block -> block.sets.sumOf { it.reps * (it.weightKg ?: 0.0) } }
}

data class RestTimer(val remaining: Int, val total: Int)

const val DEFAULT_REST_SECONDS = 90

@OptIn(ExperimentalCoroutinesApi::class)
class LiftViewModel(
    savedStateHandle: SavedStateHandle,
    private val liftRepository: LiftRepository,
    programRepository: ProgramRepository,
    private val exerciseRepository: ExerciseRepository,
) : BaseViewModel() {
    private val liftId: Long = checkNotNull(savedStateHandle.get<Long>("liftId"))

    /** Exercices ajoutés pendant la séance qui n'ont pas encore de série. */
    private val extraExercises = MutableStateFlow<List<Exercise>>(emptyList())
    private val previousSets = MutableStateFlow<Map<Long, List<LiftSet>>>(emptyMap())

    private val liftFlow: Flow<Lift?> = liftRepository.observeLift(liftId)

    private val plannedFlow: Flow<List<WorkoutExerciseDetail>> = liftFlow
        .map { it?.workoutId }
        .distinctUntilChanged()
        .flatMapLatest { workoutId ->
            if (workoutId == null) {
                flowOf(emptyList<WorkoutExerciseDetail>())
            } else {
                programRepository.observeWorkoutExercises(workoutId)
            }
        }

    val state: StateFlow<LiftUiState> = combine(
        liftFlow,
        liftRepository.observeSets(liftId),
        plannedFlow,
        extraExercises,
        previousSets,
    ) { lift, sets, planned, extras, previous ->
        LiftUiState(lift, buildBlocks(sets, planned, extras, previous), loaded = true)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, LiftUiState())

    val catalogue: StateFlow<List<Exercise>> = exerciseRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _rest = MutableStateFlow<RestTimer?>(null)
    val rest: StateFlow<RestTimer?> = _rest.asStateFlow()
    private var restJob: Job? = null
    private var restEndAt = 0L
    private var restTotal = 0

    init {
        // Charge la « dernière fois » de chaque nouvel exercice affiché.
        viewModelScope.launch {
            state.map { s -> s.blocks.map { it.exerciseId } }
                .distinctUntilChanged()
                .collect { ids ->
                    val missing = ids.filterNot { it in previousSets.value }
                    if (missing.isNotEmpty()) {
                        val loaded = missing.associateWith { liftRepository.previousSets(it, liftId) }
                        previousSets.update { it + loaded }
                    }
                }
        }
    }

    fun addSet(block: ExerciseBlock, reps: Int?, weightKg: Double?) = launchCatching {
        requireNotNull(reps) { "Indique le nombre de répétitions" }
        liftRepository.addSet(liftId, block.exerciseId, block.workoutExerciseId, reps, weightKg, block.restTimeSeconds)
        if (state.value.lift?.endTime == null) startRest(block.restTimeSeconds ?: DEFAULT_REST_SECONDS)
    }

    fun updateSet(set: LiftSet, reps: Int?, weightKg: Double?) = launchCatching {
        requireNotNull(reps) { "Indique le nombre de répétitions" }
        liftRepository.updateSet(set.copy(reps = reps, weightKg = weightKg))
    }

    fun deleteSet(set: LiftSet) = launchCatching { liftRepository.deleteSet(set) }

    fun addExercise(exercise: Exercise) {
        extraExercises.update { it + exercise }
    }

    fun createExercise(name: String) = launchCatching {
        val exercise = exerciseRepository.findOrCreate(name)
        extraExercises.update { it + exercise }
    }

    fun rename(name: String, description: String) = launchCatching {
        val lift = state.value.lift ?: return@launchCatching
        liftRepository.updateLift(lift, name, description)
    }

    fun finish() = launchCatching {
        val lift = state.value.lift ?: return@launchCatching
        skipRest()
        liftRepository.finishLift(lift)
    }

    fun delete(onDeleted: () -> Unit) = launchCatching {
        val lift = state.value.lift ?: return@launchCatching
        skipRest()
        liftRepository.deleteLift(lift)
        onDeleted()
    }

    // --- Minuteur de repos ---

    private fun startRest(seconds: Int) {
        if (seconds <= 0) return
        restTotal = seconds
        restEndAt = SystemClock.elapsedRealtime() + seconds * 1000L
        restJob?.cancel()
        restJob = viewModelScope.launch {
            while (true) {
                val left = restEndAt - SystemClock.elapsedRealtime()
                if (left <= 0) break
                _rest.value = RestTimer(((left + 999) / 1000).toInt(), restTotal)
                delay(250)
            }
            _rest.value = null
            notify("Repos terminé : série suivante !")
        }
    }

    fun extendRest(seconds: Int) {
        if (_rest.value == null) return
        restEndAt += seconds * 1000L
        restTotal += seconds
    }

    fun skipRest() {
        restJob?.cancel()
        restJob = null
        _rest.value = null
    }

    companion object {
        val Factory = appViewModelFactory {
            LiftViewModel(createSavedStateHandle(), it.liftRepository, it.programRepository, it.exerciseRepository)
        }
    }
}

/**
 * Ordre d'affichage : exercices planifiés (dans l'ordre de la séance type), puis exercices
 * hors programme dans l'ordre de leur première série, puis exercices ajoutés sans série.
 */
fun buildBlocks(
    sets: List<LiftSetDetail>,
    planned: List<WorkoutExerciseDetail>,
    extras: List<Exercise>,
    previous: Map<Long, List<LiftSet>>,
): List<ExerciseBlock> {
    val setsByExercise = sets.groupBy({ it.set.exerciseId }, { it.set })
    val blocks = LinkedHashMap<Long, ExerciseBlock>()

    planned.forEach { item ->
        val plan = item.workoutExercise
        if (plan.exerciseId !in blocks) {
            blocks[plan.exerciseId] = ExerciseBlock(
                exerciseId = plan.exerciseId,
                name = item.exerciseName,
                workoutExerciseId = plan.id,
                plannedSets = plan.plannedSets,
                plannedReps = plan.plannedReps,
                restTimeSeconds = plan.restTimeSeconds,
                sets = setsByExercise[plan.exerciseId].orEmpty(),
                previous = previous[plan.exerciseId].orEmpty(),
            )
        }
    }
    sets.forEach { detail ->
        val id = detail.set.exerciseId
        if (id !in blocks) {
            blocks[id] = ExerciseBlock(
                exerciseId = id,
                name = detail.exerciseName,
                workoutExerciseId = null,
                plannedSets = null,
                plannedReps = null,
                restTimeSeconds = null,
                sets = setsByExercise[id].orEmpty(),
                previous = previous[id].orEmpty(),
            )
        }
    }
    extras.forEach { exercise ->
        if (exercise.id !in blocks) {
            blocks[exercise.id] = ExerciseBlock(
                exerciseId = exercise.id,
                name = exercise.name,
                workoutExerciseId = null,
                plannedSets = null,
                plannedReps = null,
                restTimeSeconds = null,
                sets = emptyList(),
                previous = previous[exercise.id].orEmpty(),
            )
        }
    }
    return blocks.values.toList()
}
