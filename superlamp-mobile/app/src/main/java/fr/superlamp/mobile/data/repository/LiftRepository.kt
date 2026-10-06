package fr.superlamp.mobile.data.repository

import androidx.room.withTransaction
import fr.superlamp.mobile.core.ParsedExercise
import fr.superlamp.mobile.core.cleanOrNull
import fr.superlamp.mobile.data.SuperLampDatabase
import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.LiftSet
import fr.superlamp.mobile.data.entity.LiftSetDetail
import fr.superlamp.mobile.data.entity.LiftSummary
import fr.superlamp.mobile.data.entity.Workout
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Séances réalisées et séries (LiftCoreService / SetCoreService). */
class LiftRepository(
    private val database: SuperLampDatabase,
    private val exerciseRepository: ExerciseRepository,
) {
    private val liftDao = database.liftDao()
    private val liftSetDao = database.liftSetDao()
    private val workoutExerciseDao = database.workoutExerciseDao()

    fun observeSummaries(): Flow<List<LiftSummary>> = liftDao.observeSummaries()

    fun observeLift(id: Long): Flow<Lift?> = liftDao.observeById(id)

    fun observeActive(): Flow<Lift?> = liftDao.observeActive()

    fun observeSets(liftId: Long): Flow<List<LiftSetDetail>> = liftSetDao.observeDetails(liftId)

    /** Démarre une séance (libre si [workout] est null) et clôt celles restées ouvertes. */
    suspend fun startLift(workout: Workout?): Long = database.withTransaction {
        liftDao.closeActiveLifts()
        liftDao.insert(
            Lift(
                name = workout?.name ?: "Séance libre",
                workoutId = workout?.id,
                startTime = LocalDateTime.now(),
            )
        )
    }

    suspend fun finishLift(lift: Lift) = liftDao.update(lift.copy(endTime = LocalDateTime.now()))

    suspend fun updateLift(lift: Lift, name: String, description: String?) {
        val cleanName = name.trim()
        require(cleanName.isNotBlank()) { "Le nom de la séance est obligatoire" }
        liftDao.update(lift.copy(name = cleanName, description = description.cleanOrNull()))
    }

    suspend fun deleteLift(lift: Lift) = liftDao.delete(lift)

    suspend fun addSet(
        liftId: Long,
        exerciseId: Long,
        workoutExerciseId: Long?,
        reps: Int,
        weightKg: Double?,
        restTimeSeconds: Int?,
    ): Long {
        validateSet(reps, weightKg)
        return liftSetDao.insert(
            LiftSet(
                liftId = liftId,
                exerciseId = exerciseId,
                workoutExerciseId = workoutExerciseId,
                order = liftSetDao.maxOrder(liftId, exerciseId) + 1,
                reps = reps,
                weightKg = weightKg,
                restTimeSeconds = restTimeSeconds,
            )
        )
    }

    suspend fun updateSet(set: LiftSet) {
        validateSet(set.reps, set.weightKg)
        liftSetDao.update(set)
    }

    suspend fun deleteSet(set: LiftSet) = liftSetDao.delete(set)

    suspend fun previousSets(exerciseId: Long, liftId: Long): List<LiftSet> =
        liftSetDao.getPreviousSets(exerciseId, liftId)

    /**
     * Enregistre une séance issue du [fr.superlamp.mobile.core.ParserService].
     * Les exercices inconnus sont ajoutés au catalogue ; si [workout] est fourni,
     * les séries sont rattachées aux exercices planifiés correspondants.
     */
    suspend fun importParsed(date: LocalDate, exercises: List<ParsedExercise>, workout: Workout?): Long =
        database.withTransaction {
            val valid = exercises
                .filter { it.name.isNotBlank() }
                .map { exercise -> exercise.copy(sets = exercise.sets.filter { it.reps > 0 }) }
                .filter { it.sets.isNotEmpty() }
            require(valid.isNotEmpty()) { "Aucune série à importer" }

            val start = if (date == LocalDate.now()) LocalDateTime.now() else date.atTime(IMPORT_TIME)
            val liftId = liftDao.insert(
                Lift(
                    name = workout?.name ?: "Séance du ${date.format(SHORT_DATE)}",
                    workoutId = workout?.id,
                    startTime = start,
                    endTime = start,
                )
            )
            valid.forEach { parsed ->
                val exercise = exerciseRepository.findOrCreate(parsed.name)
                val planned = workout?.let { workoutExerciseDao.find(it.id, exercise.id) }
                val firstOrder = liftSetDao.maxOrder(liftId, exercise.id) + 1
                parsed.sets.forEachIndexed { index, set ->
                    liftSetDao.insert(
                        LiftSet(
                            liftId = liftId,
                            exerciseId = exercise.id,
                            workoutExerciseId = planned?.id,
                            order = firstOrder + index,
                            reps = set.reps,
                            weightKg = set.weight,
                            createdAt = start,
                        )
                    )
                }
            }
            liftId
        }

    private fun validateSet(reps: Int, weightKg: Double?) {
        require(reps > 0) { "Le nombre de répétitions doit être positif" }
        require(weightKg == null || weightKg >= 0) { "Le poids ne peut pas être négatif" }
    }

    private companion object {
        val IMPORT_TIME: LocalTime = LocalTime.of(18, 0)
        val SHORT_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")
    }
}
