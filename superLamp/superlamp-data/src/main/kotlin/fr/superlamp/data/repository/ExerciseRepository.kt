package fr.superlamp.data.repository

import fr.superlamp.data.entity.Exercise
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExerciseRepository : JpaRepository<Exercise, Long> {
    fun findByMuscleGroup(muscleGroup: String): List<Exercise>
}
