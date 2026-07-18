package fr.superlamp.core.service

import fr.superlamp.core.exception.WorkoutNotFoundException
import fr.superlamp.core.model.Workout
import fr.superlamp.core.ports.WorkoutRepositoryPort

/**
 * Service métier pour la gestion des Workouts
 * Contient toute la logique métier liée aux workouts
 */
class WorkoutCoreService(
    private val workoutRepository: WorkoutRepositoryPort
) {
    fun createWorkout(workout: Workout): Workout {
        // Validation métier
        require(workout.name.isNotBlank()) { "Workout name cannot be empty" }
        require(workout.order >= 0) { "Workout order must be positive" }
        require(workout.splitId > 0) { "Split ID must be valid" }
        
        return workoutRepository.save(workout)
    }

    fun getWorkoutById(id: Long): Workout {
        return workoutRepository.findById(id) 
            ?: throw WorkoutNotFoundException(id)
    }

    fun getAllWorkouts(): List<Workout> {
        return workoutRepository.findAll()
    }

    fun getWorkoutsBySplitId(splitId: Long): List<Workout> {
        return workoutRepository.findBySplitId(splitId)
    }

    fun updateWorkout(id: Long, workout: Workout): Workout {
        val existing = workoutRepository.findById(id)
            ?: throw WorkoutNotFoundException(id)
        
        // Validation métier
        require(workout.name.isNotBlank()) { "Workout name cannot be empty" }
        require(workout.order >= 0) { "Workout order must be positive" }
        require(workout.splitId > 0) { "Split ID must be valid" }
        
        val updated = existing.copy(
            splitId = workout.splitId,
            name = workout.name,
            description = workout.description,
            order = workout.order
        )
        
        return workoutRepository.save(updated)
    }

    fun deleteWorkout(id: Long) {
        if (!workoutRepository.findById(id).let { it != null }) {
            throw WorkoutNotFoundException(id)
        }
        workoutRepository.deleteById(id)
    }
}
