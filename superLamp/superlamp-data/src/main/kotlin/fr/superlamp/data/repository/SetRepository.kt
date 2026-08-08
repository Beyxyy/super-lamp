package fr.superlamp.data.repository

import fr.superlamp.data.entity.SetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SetRepository : JpaRepository<SetEntity, Long> {
    fun findByLiftId(liftId: Long): List<SetEntity>
    fun findByWorkoutExerciseId(workoutExerciseId: Long): List<SetEntity>
}
