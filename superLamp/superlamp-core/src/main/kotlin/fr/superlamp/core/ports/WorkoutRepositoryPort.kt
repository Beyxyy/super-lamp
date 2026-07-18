package fr.superlamp.core.ports

import fr.superlamp.core.model.Workout

/**
 * Port (interface) pour le repository Workout
 * à implémenter par le module superlamp-data
 */
interface WorkoutRepositoryPort {
    fun save(workout: Workout): Workout
    fun findById(id: Long): Workout?
    fun findAll(): List<Workout>
    fun findBySplitId(splitId: Long): List<Workout>
    fun deleteById(id: Long)
}
