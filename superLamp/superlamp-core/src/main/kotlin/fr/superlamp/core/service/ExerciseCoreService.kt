package fr.superlamp.core.service

import fr.superlamp.core.exception.ExerciseNotFoundException
import fr.superlamp.core.model.Exercise
import fr.superlamp.core.ports.ExerciseRepositoryPort

/**
 * Service métier pour la gestion des Exercises
 */
class ExerciseCoreService(
    private val exerciseRepository: ExerciseRepositoryPort
) {
    fun createExercise(exercise: Exercise): Exercise {
        require(exercise.name.isNotBlank()) { "Exercise name cannot be empty" }
        return exerciseRepository.save(exercise)
    }

    fun getExerciseById(id: Long): Exercise {
        return exerciseRepository.findById(id)
            ?: throw ExerciseNotFoundException(id)
    }

    fun getAllExercises(): List<Exercise> {
        return exerciseRepository.findAll()
    }

    fun getExercisesByMuscleGroup(muscleGroup: String): List<Exercise> {
        return exerciseRepository.findByMuscleGroup(muscleGroup)
    }

    fun updateExercise(id: Long, exercise: Exercise): Exercise {
        val existing = exerciseRepository.findById(id)
            ?: throw ExerciseNotFoundException(id)
        
        require(exercise.name.isNotBlank()) { "Exercise name cannot be empty" }
        
        val updated = existing.copy(
            name = exercise.name,
            description = exercise.description,
            muscleGroup = exercise.muscleGroup
        )
        
        return exerciseRepository.save(updated)
    }

    fun deleteExercise(id: Long) {
        if (!exerciseRepository.findById(id).let { it != null }) {
            throw ExerciseNotFoundException(id)
        }
        exerciseRepository.deleteById(id)
    }
}
