package fr.superlamp.core.ports

import fr.superlamp.core.model.WorkoutExercise

/**
 * Port (interface) pour le repository WorkoutExercise
 */
interface WorkoutExerciseRepositoryPort {
    fun save(workoutExercise: WorkoutExercise): WorkoutExercise
    fun findById(id: Long): WorkoutExercise?
    fun findAll(): List<WorkoutExercise>
    fun findByWorkoutId(workoutId: Long): List<WorkoutExercise>
    fun deleteById(id: Long)
}
