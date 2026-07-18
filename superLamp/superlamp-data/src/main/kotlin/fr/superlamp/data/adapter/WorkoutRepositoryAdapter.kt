package fr.superlamp.data.adapter

import fr.superlamp.core.model.Workout as WorkoutModel
import fr.superlamp.core.ports.WorkoutRepositoryPort
import fr.superlamp.data.entity.Split as SplitEntity
import fr.superlamp.data.mapper.toEntity
import fr.superlamp.data.mapper.toModel
import fr.superlamp.data.repository.SplitRepository
import fr.superlamp.data.repository.WorkoutRepository
import org.springframework.stereotype.Component

/**
 * Adapter qui implémente WorkoutRepositoryPort
 * Pont entre le module Core (ports) et le module Data (JPA)
 */
@Component
class WorkoutRepositoryAdapter(
    private val workoutRepository: WorkoutRepository,
    private val splitRepository: SplitRepository
) : WorkoutRepositoryPort {
    
    override fun save(workout: WorkoutModel): WorkoutModel {
        val split = splitRepository.findById(workout.splitId)
            .orElseThrow { IllegalArgumentException("Split with id ${workout.splitId} not found") }
        val entity = workout.toEntity(split)
        val savedEntity = workoutRepository.save(entity)
        return savedEntity.toModel()
    }

    override fun findById(id: Long): WorkoutModel? {
        return workoutRepository.findById(id).orElse(null)?.toModel()
    }

    override fun findAll(): List<WorkoutModel> {
        return workoutRepository.findAll().map { it.toModel() }
    }

    override fun findBySplitId(splitId: Long): List<WorkoutModel> {
        return workoutRepository.findBySplitId(splitId).map { it.toModel() }
    }

    override fun deleteById(id: Long) {
        workoutRepository.deleteById(id)
    }
}
