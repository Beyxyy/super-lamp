package fr.superlamp.data.repository

import fr.superlamp.data.entity.ExerciseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExerciseRepository : JpaRepository<ExerciseEntity, Long> {
    fun findByMuscleGroup(muscleGroup: String): List<ExerciseEntity>
}
