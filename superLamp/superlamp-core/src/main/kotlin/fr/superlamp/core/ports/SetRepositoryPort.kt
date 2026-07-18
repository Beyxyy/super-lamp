package fr.superlamp.core.ports

import fr.superlamp.core.model.Set

/**
 * Port (interface) pour le repository Set
 */
interface SetRepositoryPort {
    fun save(set: Set): Set
    fun findById(id: Long): Set?
    fun findAll(): List<Set>
    fun findByLiftId(liftId: Long): List<Set>
    fun findByWorkoutExerciseId(workoutExerciseId: Long): List<Set>
    fun deleteById(id: Long)
}
