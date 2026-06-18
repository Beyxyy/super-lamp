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
    
    fun updateWorkout(id: Long, workout: Workout): Workout? {
        return workoutRepository.findById(id).map {
            val updated = Workout(
                id = it.id,
                name = workout.name,
                description = workout.description,
                startTime = workout.startTime,
                endTime = workout.endTime,
                durationSeconds = workout.durationSeconds,
                caloriesBurned = workout.caloriesBurned
            )
            workoutRepository.save(updated)
        }.orElse(null)
    }
    
    fun deleteWorkout(id: Long) {
        workoutRepository.deleteById(id)
    }
}
