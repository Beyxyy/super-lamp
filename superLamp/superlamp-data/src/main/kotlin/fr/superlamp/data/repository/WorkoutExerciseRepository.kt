package fr.superlamp.data.repository

import fr.superlamp.data.entity.WorkoutExercise
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutExerciseRepository : JpaRepository<WorkoutExercise, Long> {
    fun findByWorkoutId(workoutId: Long): List<WorkoutExercise>
}
