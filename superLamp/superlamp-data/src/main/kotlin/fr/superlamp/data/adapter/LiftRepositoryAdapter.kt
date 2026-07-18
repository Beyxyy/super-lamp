package fr.superlamp.data.adapter

import fr.superlamp.core.model.Lift as LiftModel
import fr.superlamp.core.ports.LiftRepositoryPort
import fr.superlamp.data.entity.Workout as WorkoutEntity
import fr.superlamp.data.mapper.toEntity
import fr.superlamp.data.mapper.toModel
import fr.superlamp.data.repository.LiftRepository
import fr.superlamp.data.repository.WorkoutRepository
import org.springframework.stereotype.Component

/**
 * Adapter pour LiftRepositoryPort
 */
@Component
class LiftRepositoryAdapter(
    private val liftRepository: LiftRepository,
    private val workoutRepository: WorkoutRepository
) : LiftRepositoryPort {
    
    override fun save(lift: LiftModel): LiftModel {
        val workout = workoutRepository.findById(lift.workoutId)
            .orElseThrow { IllegalArgumentException("Workout with id ${lift.workoutId} not found") }
        val entity = lift.toEntity(workout)
        val savedEntity = liftRepository.save(entity)
        return savedEntity.toModel()
    }

    override fun findById(id: Long): LiftModel? {
        return liftRepository.findById(id).orElse(null)?.toModel()
    }

    override fun findAll(): List<LiftModel> {
        return liftRepository.findAll().map { it.toModel() }
    }

    override fun findByUserId(userId: Long): List<LiftModel> {
        return liftRepository.findByUserId(userId).map { it.toModel() }
    }

    override fun findByWorkoutId(workoutId: Long): List<LiftModel> {
        return liftRepository.findByWorkoutId(workoutId).map { it.toModel() }
    }

    override fun deleteById(id: Long) {
        liftRepository.deleteById(id)
    }
}
