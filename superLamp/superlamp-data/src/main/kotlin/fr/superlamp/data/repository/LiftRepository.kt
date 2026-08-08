package fr.superlamp.data.repository

import fr.superlamp.data.entity.LiftEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LiftRepository : JpaRepository<LiftEntity, Long> {
    fun findByUserId(userId: Long): List<LiftEntity>
    fun findByWorkoutId(workoutId: Long): List<LiftEntity>
}
