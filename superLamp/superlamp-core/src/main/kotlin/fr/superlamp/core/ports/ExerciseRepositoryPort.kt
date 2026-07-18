package fr.superlamp.core.ports

import fr.superlamp.core.model.Exercise

/**
 * Port (interface) pour le repository Exercise
 */
interface ExerciseRepositoryPort {
    fun save(exercise: Exercise): Exercise
    fun findById(id: Long): Exercise?
    fun findAll(): List<Exercise>
    fun findByMuscleGroup(muscleGroup: String): List<Exercise>
    fun deleteById(id: Long)
}
