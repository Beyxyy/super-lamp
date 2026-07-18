package fr.superlamp.data.service

import fr.superlamp.data.entity.Workout
import fr.superlamp.data.repository.WorkoutRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkoutService(private val workoutRepository: WorkoutRepository) {

    fun createWorkout(workout: Workout): Workout {
        return workoutRepository.save(workout)
    }

    fun getWorkoutById(id: Long): Workout? {
        return workoutRepository.findById(id).orElse(null)
    }

    fun getAllWorkouts(): List<Workout> {
        return workoutRepository.findAll()
    }

    fun getWorkoutsBySplitId(splitId: Long): List<Workout> {
        return workoutRepository.findBySplitId(splitId)
    }

    fun updateWorkout(id: Long, workout: Workout): Workout? {
        val existing = workoutRepository.findById(id).orElse(null) ?: return null
        return workoutRepository.save(
            existing.copy(
                split = workout.split,
                name = workout.name,
                description = workout.description,
                order = workout.order
            )
        )
    }

    fun deleteWorkout(id: Long) {
        workoutRepository.deleteById(id)
    }
}
