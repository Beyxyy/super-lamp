package fr.superlamp.data.adapter

import fr.superlamp.core.model.Set as SetModel
import fr.superlamp.core.ports.SetRepositoryPort
import fr.superlamp.data.entity.Lift as LiftEntity
import fr.superlamp.data.entity.WorkoutExercise as WorkoutExerciseEntity
import fr.superlamp.data.mapper.toEntity
import fr.superlamp.data.mapper.toModel
import fr.superlamp.data.repository.LiftRepository
import fr.superlamp.data.repository.SetRepository
import fr.superlamp.data.repository.WorkoutExerciseRepository
import org.springframework.stereotype.Component

/**
 * Adapter pour SetRepositoryPort
 */
@Component
class SetRepositoryAdapter(
    private val setRepository: SetRepository,
    private val liftRepository: LiftRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository
) : SetRepositoryPort {
    
    override fun save(set: SetModel): SetModel {
        val lift = liftRepository.findById(set.liftId)
            .orElseThrow { IllegalArgumentException("Lift with id ${set.liftId} not found") }
        val workoutExercise = workoutExerciseRepository.findById(set.workoutExerciseId)
            .orElseThrow { IllegalArgumentException("WorkoutExercise with id ${set.workoutExerciseId} not found") }
        
        val entity = set.toEntity(lift, workoutExercise)
        val savedEntity = setRepository.save(entity)
        return savedEntity.toModel()
    }

    override fun findById(id: Long): SetModel? {
        return setRepository.findById(id).orElse(null)?.toModel()
    }

    override fun findAll(): List<SetModel> {
        return setRepository.findAll().map { it.toModel() }
    }

    override fun findByLiftId(liftId: Long): List<SetModel> {
        return setRepository.findByLiftId(liftId).map { it.toModel() }
    }

    override fun findByWorkoutExerciseId(workoutExerciseId: Long): List<SetModel> {
        return setRepository.findByWorkoutExerciseId(workoutExerciseId).map { it.toModel() }
    }

    override fun deleteById(id: Long) {
        setRepository.deleteById(id)
    }
}
