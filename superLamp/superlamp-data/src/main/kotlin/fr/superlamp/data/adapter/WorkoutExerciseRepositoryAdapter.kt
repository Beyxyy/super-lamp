package fr.superlamp.data.adapter

import fr.superlamp.core.model.WorkoutExercise as WorkoutExerciseModel
import fr.superlamp.core.ports.WorkoutExerciseRepositoryPort
import fr.superlamp.data.entity.Exercise as ExerciseEntity
import fr.superlamp.data.entity.Workout as WorkoutEntity
import fr.superlamp.data.mapper.toEntity
import fr.superlamp.data.mapper.toModel
import fr.superlamp.data.repository.ExerciseRepository
import fr.superlamp.data.repository.WorkoutExerciseRepository
import fr.superlamp.data.repository.WorkoutRepository
import org.springframework.stereotype.Component

/**
 * Adapter pour WorkoutExerciseRepositoryPort
 */
@Component
class WorkoutExerciseRepositoryAdapter(
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : WorkoutExerciseRepositoryPort {
    
    override fun save(workoutExercise: WorkoutExerciseModel): WorkoutExerciseModel {
        val workout = workoutRepository.findById(workoutExercise.workoutId)
            .orElseThrow { IllegalArgumentException("Workout with id ${workoutExercise.workoutId} not found") }
        val exercise = exerciseRepository.findById(workoutExercise.exerciseId)
            .orElseThrow { IllegalArgumentException("Exercise with id ${workoutExercise.exerciseId} not found") }
        
        val entity = workoutExercise.toEntity(workout, exercise)
        val savedEntity = workoutExerciseRepository.save(entity)
        return savedEntity.toModel()
    }

    override fun findById(id: Long): WorkoutExerciseModel? {
        return workoutExerciseRepository.findById(id).orElse(null)?.toModel()
    }

    override fun findAll(): List<WorkoutExerciseModel> {
        return workoutExerciseRepository.findAll().map { it.toModel() }
    }

    override fun findByWorkoutId(workoutId: Long): List<WorkoutExerciseModel> {
        return workoutExerciseRepository.findByWorkoutId(workoutId).map { it.toModel() }
    }

    override fun deleteById(id: Long) {
        workoutExerciseRepository.deleteById(id)
    }
}
