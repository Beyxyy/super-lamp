package fr.superlamp.data.service

import fr.superlamp.data.entity.Lift
import fr.superlamp.data.repository.LiftRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LiftService(private val liftRepository: LiftRepository) {

    fun createLift(lift: Lift): Lift {
        return liftRepository.save(lift)
    }

    fun getLiftById(id: Long): Lift? {
        return liftRepository.findById(id).orElse(null)
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

    fun updateLift(id: Long, lift: Lift): Lift? {
        return liftRepository.findById(id).map { existing ->
            val updated = existing.copy(
                name = lift.name,
                description = lift.description,
                userId = lift.userId,
                workout = lift.workout,
                startTime = lift.startTime,
                endTime = lift.endTime
            )
            liftRepository.save(updated)
        }.orElse(null)
    }

    fun deleteLift(id: Long) {
        liftRepository.deleteById(id)
    }
}
