@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.entity.Workout
import fr.superlamp.mobile.data.entity.WorkoutExerciseDetail
import fr.superlamp.mobile.data.repository.ExerciseRepository
import fr.superlamp.mobile.data.repository.LiftRepository
import fr.superlamp.mobile.data.repository.ProgramRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.BackButton
import fr.superlamp.mobile.ui.components.ConfirmDialog
import fr.superlamp.mobile.ui.components.EmptyState
import fr.superlamp.mobile.ui.components.ExercisePickerDialog
import fr.superlamp.mobile.ui.components.FormDialog
import fr.superlamp.mobile.ui.components.NameDescriptionDialog
import fr.superlamp.mobile.ui.components.NumberField
import fr.superlamp.mobile.ui.plannedLabel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class WorkoutDetailState(
    val workout: Workout? = null,
    val exercises: List<WorkoutExerciseDetail> = emptyList(),
    val loaded: Boolean = false,
)

class WorkoutDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val programRepository: ProgramRepository,
    private val exerciseRepository: ExerciseRepository,
    private val liftRepository: LiftRepository,
) : BaseViewModel() {
    private val workoutId: Long = checkNotNull(savedStateHandle.get<Long>("workoutId"))

    val state: StateFlow<WorkoutDetailState> = combine(
        programRepository.observeWorkout(workoutId),
        programRepository.observeWorkoutExercises(workoutId),
    ) { workout, exercises -> WorkoutDetailState(workout, exercises, loaded = true) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutDetailState())

    val catalogue: StateFlow<List<Exercise>> = exerciseRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun update(name: String, description: String) = launchCatching {
        val workout = state.value.workout ?: return@launchCatching
        programRepository.updateWorkout(workout, name, description)
    }

    fun delete(onDeleted: () -> Unit) = launchCatching {
        val workout = state.value.workout ?: return@launchCatching
        programRepository.deleteWorkout(workout)
        onDeleted()
    }

    fun createExercise(name: String, onCreated: (Exercise) -> Unit) = launchCatching {
        onCreated(exerciseRepository.findOrCreate(name))
    }

    fun addExercise(exercise: Exercise, sets: Int, reps: Int?, rest: Int?) = launchCatching {
        programRepository.addExerciseToWorkout(workoutId, exercise.id, sets, reps, rest)
    }

    fun updatePlan(item: WorkoutExerciseDetail, sets: Int, reps: Int?, rest: Int?) = launchCatching {
        programRepository.updateWorkoutExercise(item.workoutExercise, sets, reps, rest)
    }

    fun remove(item: WorkoutExerciseDetail) = launchCatching {
        programRepository.removeWorkoutExercise(item.workoutExercise)
    }

    fun move(item: WorkoutExerciseDetail, direction: Int) = launchCatching {
        programRepository.moveWorkoutExercise(item.workoutExercise, direction)
    }

    fun start(onStarted: (Long) -> Unit) = launchCatching {
        val workout = state.value.workout ?: return@launchCatching
        onStarted(liftRepository.startLift(workout))
    }

    companion object {
        val Factory = appViewModelFactory {
            WorkoutDetailViewModel(createSavedStateHandle(), it.programRepository, it.exerciseRepository, it.liftRepository)
        }
    }
}

@Composable
fun WorkoutDetailScreen(
    onBack: () -> Unit,
    onLiftStarted: (Long) -> Unit,
    viewModel: WorkoutDetailViewModel = viewModel(factory = WorkoutDetailViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val catalogue by viewModel.catalogue.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)
    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var pendingExercise by remember { mutableStateOf<Exercise?>(null) }
    var editing by remember { mutableStateOf<WorkoutExerciseDetail?>(null) }
    val workout = state.workout

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name.orEmpty()) },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    if (workout != null) {
                        IconButton(onClick = { showEdit = true }) { Icon(Icons.Default.Edit, contentDescription = "Modifier") }
                        IconButton(onClick = { showDelete = true }) { Icon(Icons.Default.Delete, contentDescription = "Supprimer") }
                    }
                },
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Button(
                    onClick = { viewModel.start(onLiftStarted) },
                    enabled = workout != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Démarrer cette séance")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            workout?.description?.let { description ->
                item(key = "description") {
                    Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (state.loaded && state.exercises.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        icon = Icons.Default.FitnessCenter,
                        title = "Aucun exercice",
                        message = "Ajoute les exercices de cette séance avec leurs séries, répétitions et temps de repos visés.",
                    )
                }
            }
            itemsIndexed(state.exercises, key = { _, item -> item.workoutExercise.id }) { index, item ->
                PlannedExerciseRow(
                    position = index + 1,
                    item = item,
                    canMoveUp = index > 0,
                    canMoveDown = index < state.exercises.lastIndex,
                    onEdit = { editing = item },
                    onRemove = { viewModel.remove(item) },
                    onMove = { direction -> viewModel.move(item, direction) },
                )
            }
            item(key = "add") {
                OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Ajouter un exercice")
                }
            }
        }
    }

    if (showPicker) {
        val alreadyPlanned = state.exercises.map { it.workoutExercise.exerciseId }.toSet()
        ExercisePickerDialog(
            exercises = catalogue.filter { it.id !in alreadyPlanned },
            onDismiss = { showPicker = false },
            onPick = {
                showPicker = false
                pendingExercise = it
            },
            onCreate = { name ->
                showPicker = false
                viewModel.createExercise(name) { pendingExercise = it }
            },
        )
    }
    pendingExercise?.let { exercise ->
        PlanDialog(
            title = exercise.name,
            onDismiss = { pendingExercise = null },
            onConfirm = { sets, reps, rest ->
                pendingExercise = null
                viewModel.addExercise(exercise, sets, reps, rest)
            },
        )
    }
    editing?.let { item ->
        PlanDialog(
            title = item.exerciseName,
            initialSets = item.workoutExercise.plannedSets,
            initialReps = item.workoutExercise.plannedReps,
            initialRest = item.workoutExercise.restTimeSeconds,
            onDismiss = { editing = null },
            onConfirm = { sets, reps, rest ->
                editing = null
                viewModel.updatePlan(item, sets, reps, rest)
            },
        )
    }
    if (showEdit && workout != null) {
        NameDescriptionDialog(
            title = "Modifier la séance type",
            initialName = workout.name,
            initialDescription = workout.description.orEmpty(),
            onDismiss = { showEdit = false },
            onConfirm = { name, description ->
                showEdit = false
                viewModel.update(name, description)
            },
        )
    }
    if (showDelete) {
        ConfirmDialog(
            title = "Supprimer la séance type ?",
            message = "Les séances déjà réalisées restent dans l'historique.",
            onDismiss = { showDelete = false },
            onConfirm = { viewModel.delete(onBack) },
        )
    }
}

@Composable
private fun PlannedExerciseRow(
    position: Int,
    item: WorkoutExerciseDetail,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onMove: (Int) -> Unit,
) {
    val plan = item.workoutExercise
    ElevatedCard(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("$position. ${item.exerciseName}", style = MaterialTheme.typography.titleMedium)
                Text(
                    plannedLabel(plan.plannedSets, plan.plannedReps, plan.restTimeSeconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                item.muscleGroup?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column {
                IconButton(onClick = { onMove(-1) }, enabled = canMoveUp, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Monter")
                }
                IconButton(onClick = { onMove(1) }, enabled = canMoveDown, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Descendre")
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Retirer de la séance")
            }
        }
    }
}

/** Séries / reps / repos visés pour un exercice d'une séance type. */
@Composable
private fun PlanDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (sets: Int, reps: Int?, rest: Int?) -> Unit,
    initialSets: Int = 3,
    initialReps: Int? = 10,
    initialRest: Int? = 90,
) {
    var sets by rememberSaveable { mutableStateOf(initialSets.toString()) }
    var reps by rememberSaveable { mutableStateOf(initialReps?.toString().orEmpty()) }
    var rest by rememberSaveable { mutableStateOf(initialRest?.toString().orEmpty()) }
    val setsValue = sets.toIntOrNull()

    FormDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(setsValue ?: 0, reps.toIntOrNull(), rest.toIntOrNull()) },
        confirmEnabled = setsValue != null && setsValue > 0,
    ) {
        NumberField(sets, { sets = it }, "Séries", Modifier.fillMaxWidth())
        NumberField(reps, { reps = it }, "Répétitions visées (optionnel)", Modifier.fillMaxWidth())
        NumberField(rest, { rest = it }, "Repos en secondes (optionnel)", Modifier.fillMaxWidth())
    }
}
