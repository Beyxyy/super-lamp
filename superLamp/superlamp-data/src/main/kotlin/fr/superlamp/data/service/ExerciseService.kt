package fr.superlamp.data.service

import fr.superlamp.data.entity.Exercise
import fr.superlamp.data.repository.ExerciseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ExerciseService(private val exerciseRepository: ExerciseRepository) {

    fun createExercise(exercise: Exercise): Exercise {
        return exerciseRepository.save(exercise)
    }

    fun getExerciseById(id: Long): Exercise? {
        return exerciseRepository.findById(id).orElse(null)
    }

    fun getAllExercises(): List<Exercise> {
        return exerciseRepository.findAll()
    }

    fun updateExercise(id: Long, exercise: Exercise): Exercise? {
       val existing = exerciseRepository.findById(id).orElse(null) ?: return null
        return exerciseRepository.save(existing.copy(
                name = exercise.name,
                description = exercise.description,
                muscleGroup = exercise.muscleGroup
        ))
    }

    fun deleteExercise(id: Long) {
       exerciseRepository.deleteById(id)
    }

    fun getExercisesByMuscleGroup(muscleGroup: String): List<Exercise> {
        return exerciseRepository.findByMuscleGroup(muscleGroup)
    }
}
