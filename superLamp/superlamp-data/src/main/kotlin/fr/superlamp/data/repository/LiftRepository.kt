package fr.superlamp.data.repository

import fr.superlamp.data.entity.Lift
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LiftRepository : JpaRepository<Lift, Long> {
    fun findByUserId(userId: Long): List<Lift>
    fun findByWorkoutId(workoutId: Long): List<Lift>
}
