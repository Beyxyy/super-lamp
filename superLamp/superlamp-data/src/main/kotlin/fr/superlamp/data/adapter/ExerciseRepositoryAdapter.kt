package fr.superlamp.data.adapter

import fr.superlamp.core.model.Exercise as ExerciseModel
import fr.superlamp.core.ports.ExerciseRepositoryPort
import fr.superlamp.data.mapper.toEntity
import fr.superlamp.data.mapper.toModel
import fr.superlamp.data.repository.ExerciseRepository
import org.springframework.stereotype.Component

/**
 * Adapter pour ExerciseRepositoryPort
 */
@Component
class ExerciseRepositoryAdapter(
    private val exerciseRepository: ExerciseRepository
) : ExerciseRepositoryPort {
    
    override fun save(exercise: ExerciseModel): ExerciseModel {
        val entity = exercise.toEntity()
        val savedEntity = exerciseRepository.save(entity)
        return savedEntity.toModel()
    }

    override fun findById(id: Long): ExerciseModel? {
        return exerciseRepository.findById(id).orElse(null)?.toModel()
    }

    override fun findAll(): List<ExerciseModel> {
        return exerciseRepository.findAll().map { it.toModel() }
    }

    override fun findByMuscleGroup(muscleGroup: String): List<ExerciseModel> {
        return exerciseRepository.findByMuscleGroup(muscleGroup).map { it.toModel() }
    }

    override fun deleteById(id: Long) {
        exerciseRepository.deleteById(id)
    }
}
