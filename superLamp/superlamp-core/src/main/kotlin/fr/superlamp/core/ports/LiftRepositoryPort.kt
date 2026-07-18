package fr.superlamp.core.ports

import fr.superlamp.core.model.Lift

/**
 * Port (interface) pour le repository Lift
 */
interface LiftRepositoryPort {
    fun save(lift: Lift): Lift
    fun findById(id: Long): Lift?
    fun findAll(): List<Lift>
    fun findByUserId(userId: Long): List<Lift>
    fun findByWorkoutId(workoutId: Long): List<Lift>
    fun deleteById(id: Long)
}
