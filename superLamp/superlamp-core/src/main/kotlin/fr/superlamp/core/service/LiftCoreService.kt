package fr.superlamp.core.service

import fr.superlamp.core.exception.LiftNotFoundException
import fr.superlamp.core.model.Lift
import fr.superlamp.core.ports.LiftRepositoryPort
import java.time.LocalDateTime

/**
 * Service métier pour la gestion des Lifts
 */
class LiftCoreService(
    private val liftRepository: LiftRepositoryPort
) {
    fun createLift(lift: Lift): Lift {
        require(lift.name.isNotBlank()) { "Lift name cannot be empty" }
        require(lift.userId > 0) { "User ID must be valid" }
        require(lift.workoutId > 0) { "Workout ID must be valid" }
        require(lift.startTime <= LocalDateTime.now()) { "Start time cannot be in the future" }
        
        return liftRepository.save(lift)
    }

    fun getLiftById(id: Long): Lift {
        return liftRepository.findById(id)
            ?: throw LiftNotFoundException(id)
    }

    fun getAllLifts(): List<Lift> {
        return liftRepository.findAll()
    }

    fun getLiftsByUserId(userId: Long): List<Lift> {
        return liftRepository.findByUserId(userId)
    }

    fun getLiftsByWorkoutId(workoutId: Long): List<Lift> {
        return liftRepository.findByWorkoutId(workoutId)
    }

    fun updateLift(id: Long, lift: Lift): Lift {
        val existing = liftRepository.findById(id)
            ?: throw LiftNotFoundException(id)
        
        require(lift.name.isNotBlank()) { "Lift name cannot be empty" }
        require(lift.userId > 0) { "User ID must be valid" }
        require(lift.workoutId > 0) { "Workout ID must be valid" }
        
        val updated = existing.copy(
            name = lift.name,
            description = lift.description,
            userId = lift.userId,
            workoutId = lift.workoutId,
            startTime = lift.startTime,
            endTime = lift.endTime
        )
        
        return liftRepository.save(updated)
    }

    fun deleteLift(id: Long) {
        if (!liftRepository.findById(id).let { it != null }) {
            throw LiftNotFoundException(id)
        }
        liftRepository.deleteById(id)
    }
}
