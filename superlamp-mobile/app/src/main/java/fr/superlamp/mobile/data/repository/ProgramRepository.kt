package fr.superlamp.mobile.data.repository

import fr.superlamp.mobile.core.cleanOrNull
import fr.superlamp.mobile.core.moved
import fr.superlamp.mobile.data.dao.LiftDao
import fr.superlamp.mobile.data.dao.SplitDao
import fr.superlamp.mobile.data.dao.WorkoutDao
import fr.superlamp.mobile.data.dao.WorkoutExerciseDao
import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.Split
import fr.superlamp.mobile.data.entity.SplitSummary
import fr.superlamp.mobile.data.entity.Workout
import fr.superlamp.mobile.data.entity.WorkoutExercise
import fr.superlamp.mobile.data.entity.WorkoutExerciseDetail
import fr.superlamp.mobile.data.entity.WorkoutWithSplit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Programmes, séances types et exercices planifiés (Split/Workout/WorkoutExerciseCoreService). */
class ProgramRepository(
    private val splitDao: SplitDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val liftDao: LiftDao,
) {
    // --- Split ---

    fun observeSplits(): Flow<List<SplitSummary>> = splitDao.observeSummaries()

    fun observeSplit(id: Long): Flow<Split?> = splitDao.observeById(id)

    suspend fun createSplit(name: String, description: String?): Long {
        val cleanName = name.trim()
        require(cleanName.isNotBlank()) { "Le nom du programme est obligatoire" }
        require(splitDao.findByName(cleanName) == null) { "Le programme « $cleanName » existe déjà" }
        return splitDao.insert(Split(name = cleanName, description = description.cleanOrNull()))
    }

    suspend fun updateSplit(split: Split, name: String, description: String?) {
        val cleanName = name.trim()
        require(cleanName.isNotBlank()) { "Le nom du programme est obligatoire" }
        val homonym = splitDao.findByName(cleanName)
        require(homonym == null || homonym.id == split.id) { "Le programme « $cleanName » existe déjà" }
        splitDao.update(split.copy(name = cleanName, description = description.cleanOrNull()))
    }

    suspend fun deleteSplit(split: Split) = splitDao.delete(split)

    // --- Workout ---

    fun observeWorkouts(splitId: Long): Flow<List<Workout>> = workoutDao.observeBySplit(splitId)

    fun observeWorkout(id: Long): Flow<Workout?> = workoutDao.observeById(id)

    fun observeAllWorkouts(): Flow<List<WorkoutWithSplit>> = workoutDao.observeAllWithSplit()

    suspend fun createWorkout(splitId: Long, name: String, description: String?): Long {
        val cleanName = name.trim()
        require(cleanName.isNotBlank()) { "Le nom de la séance est obligatoire" }
        return workoutDao.insert(
            Workout(
                splitId = splitId,
                name = cleanName,
                description = description.cleanOrNull(),
                order = workoutDao.maxOrder(splitId) + 1,
            )
        )
    }

    suspend fun updateWorkout(workout: Workout, name: String, description: String?) {
        val cleanName = name.trim()
        require(cleanName.isNotBlank()) { "Le nom de la séance est obligatoire" }
        workoutDao.update(workout.copy(name = cleanName, description = description.cleanOrNull()))
    }

    suspend fun deleteWorkout(workout: Workout) = workoutDao.delete(workout)

    suspend fun moveWorkout(workout: Workout, direction: Int) {
        val workouts = workoutDao.getBySplit(workout.splitId)
        val reordered = workouts.moved(workouts.indexOfFirst { it.id == workout.id }, direction) ?: return
        workoutDao.updateAll(reordered.mapIndexed { index, w -> w.copy(order = index) })
    }

    // --- WorkoutExercise ---

    fun observeWorkoutExercises(workoutId: Long): Flow<List<WorkoutExerciseDetail>> =
        workoutExerciseDao.observeDetails(workoutId)

    suspend fun addExerciseToWorkout(
        workoutId: Long,
        exerciseId: Long,
        plannedSets: Int,
        plannedReps: Int?,
        restTimeSeconds: Int?,
    ) {
        validatePlan(plannedSets, plannedReps, restTimeSeconds)
        require(workoutExerciseDao.find(workoutId, exerciseId) == null) { "Cet exercice est déjà dans la séance" }
        workoutExerciseDao.insert(
            WorkoutExercise(
                workoutId = workoutId,
                exerciseId = exerciseId,
                order = workoutExerciseDao.maxOrder(workoutId) + 1,
                plannedSets = plannedSets,
                plannedReps = plannedReps,
                restTimeSeconds = restTimeSeconds,
            )
        )
    }

    suspend fun updateWorkoutExercise(
        workoutExercise: WorkoutExercise,
        plannedSets: Int,
        plannedReps: Int?,
        restTimeSeconds: Int?,
    ) {
        validatePlan(plannedSets, plannedReps, restTimeSeconds)
        workoutExerciseDao.update(
            workoutExercise.copy(
                plannedSets = plannedSets,
                plannedReps = plannedReps,
                restTimeSeconds = restTimeSeconds,
            )
        )
    }

    suspend fun removeWorkoutExercise(workoutExercise: WorkoutExercise) = workoutExerciseDao.delete(workoutExercise)

    suspend fun moveWorkoutExercise(workoutExercise: WorkoutExercise, direction: Int) {
        val items = workoutExerciseDao.getByWorkout(workoutExercise.workoutId)
        val reordered = items.moved(items.indexOfFirst { it.id == workoutExercise.id }, direction) ?: return
        workoutExerciseDao.updateAll(reordered.mapIndexed { index, we -> we.copy(order = index) })
    }

    private fun validatePlan(plannedSets: Int, plannedReps: Int?, restTimeSeconds: Int?) {
        require(plannedSets > 0) { "Le nombre de séries doit être positif" }
        require(plannedReps == null || plannedReps > 0) { "Le nombre de répétitions doit être positif" }
        require(restTimeSeconds == null || restTimeSeconds >= 0) { "Le temps de repos ne peut pas être négatif" }
    }

    // --- Prochaine séance (PLAN.md, CU-01) ---

    fun observeNextWorkout(): Flow<WorkoutWithSplit?> =
        combine(liftDao.observeLastWithWorkout(), workoutDao.observeAllWithSplit()) { lastLift, workouts ->
            nextWorkout(lastLift, workouts)
        }
}

/**
 * Séance suivante : celle qui suit, dans son programme, la dernière séance type réalisée
 * (en boucle). Sans historique, la première séance du premier programme.
 * [workouts] doit être trié par programme puis par ordre.
 */
fun nextWorkout(lastLift: Lift?, workouts: List<WorkoutWithSplit>): WorkoutWithSplit? {
    if (workouts.isEmpty()) return null
    val lastWorkout = lastLift?.workoutId?.let { id -> workouts.firstOrNull { it.workout.id == id } }
        ?: return workouts.first()
    val sameSplit = workouts.filter { it.workout.splitId == lastWorkout.workout.splitId }
    val index = sameSplit.indexOf(lastWorkout)
    return sameSplit[(index + 1) % sameSplit.size]
}
