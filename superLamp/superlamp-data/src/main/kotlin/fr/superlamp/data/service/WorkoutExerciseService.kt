package fr.superlamp.data.service

import fr.superlamp.data.entity.WorkoutExercise
import fr.superlamp.data.repository.WorkoutExerciseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkoutExerciseService(private val workoutExerciseRepository: WorkoutExerciseRepository) {

    fun createWorkoutExercise(workoutExercise: WorkoutExercise): WorkoutExercise {
        return workoutExerciseRepository.save(workoutExercise)
    }

    fun getWorkoutExerciseById(id: Long): WorkoutExercise? {
        return workoutExerciseRepository.findById(id).orElse(null)
    }

    fun getAllWorkoutExercises(): List<WorkoutExercise> {
        return workoutExerciseRepository.findAll()
    }

    fun getWorkoutExercisesByWorkoutId(workoutId: Long): List<WorkoutExercise> {
        return workoutExerciseRepository.findByWorkoutId(workoutId)
    }

    fun updateWorkoutExercise(id: Long, workoutExercise: WorkoutExercise): WorkoutExercise? {
        return workoutExerciseRepository.findById(id).map { existing ->
            val updated = existing.copy(
                workout = workoutExercise.workout,
                exercise = workoutExercise.exercise,
                order = workoutExercise.order,
                plannedSets = workoutExercise.plannedSets,
                plannedReps = workoutExercise.plannedReps,
                restTimeSeconds = workoutExercise.restTimeSeconds
            )
            workoutExerciseRepository.save(updated)
        }.orElse(null)
    }

    fun deleteWorkoutExercise(id: Long) {
        workoutExerciseRepository.deleteById(id)
    }
}
