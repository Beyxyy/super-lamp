package fr.superlamp.core.service

import fr.superlamp.core.exception.WorkoutExerciseNotFoundException
import fr.superlamp.core.model.WorkoutExercise
import fr.superlamp.core.ports.WorkoutExerciseRepositoryPort

/**
 * Service métier pour la gestion des WorkoutExercises
 */
class WorkoutExerciseCoreService(
    private val workoutExerciseRepository: WorkoutExerciseRepositoryPort
) {
    fun createWorkoutExercise(workoutExercise: WorkoutExercise): WorkoutExercise {
        require(workoutExercise.workoutId > 0) { "Workout ID must be valid" }
        require(workoutExercise.exerciseId > 0) { "Exercise ID must be valid" }
        require(workoutExercise.plannedSets > 0) { "Planned sets must be positive" }
        require(workoutExercise.order >= 0) { "Order must be positive" }
        
        return workoutExerciseRepository.save(workoutExercise)
    }

    fun getWorkoutExerciseById(id: Long): WorkoutExercise {
        return workoutExerciseRepository.findById(id)
            ?: throw WorkoutExerciseNotFoundException(id)
    }

    fun getAllWorkoutExercises(): List<WorkoutExercise> {
        return workoutExerciseRepository.findAll()
    }

    fun getWorkoutExercisesByWorkoutId(workoutId: Long): List<WorkoutExercise> {
        return workoutExerciseRepository.findByWorkoutId(workoutId)
    }

    fun updateWorkoutExercise(id: Long, workoutExercise: WorkoutExercise): WorkoutExercise {
        val existing = workoutExerciseRepository.findById(id)
            ?: throw WorkoutExerciseNotFoundException(id)
        
        require(workoutExercise.workoutId > 0) { "Workout ID must be valid" }
        require(workoutExercise.exerciseId > 0) { "Exercise ID must be valid" }
        require(workoutExercise.plannedSets > 0) { "Planned sets must be positive" }
        
        val updated = existing.copy(
            workoutId = workoutExercise.workoutId,
            exerciseId = workoutExercise.exerciseId,
            order = workoutExercise.order,
            plannedSets = workoutExercise.plannedSets,
            plannedReps = workoutExercise.plannedReps,
            restTimeSeconds = workoutExercise.restTimeSeconds
        )
        
        return workoutExerciseRepository.save(updated)
    }

    fun deleteWorkoutExercise(id: Long) {
        if (!workoutExerciseRepository.findById(id).let { it != null }) {
            throw WorkoutExerciseNotFoundException(id)
        }
        workoutExerciseRepository.deleteById(id)
    }
}
