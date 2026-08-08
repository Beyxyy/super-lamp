package fr.superlamp.data.repository

import fr.superlamp.data.entity.WorkoutExerciseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutExerciseRepository : JpaRepository<WorkoutExerciseEntity, Long> {
    fun findByWorkoutId(workoutId: Long): List<WorkoutExerciseEntity>
}
