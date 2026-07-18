package fr.superlamp.data.repository

import fr.superlamp.data.entity.Set
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SetRepository : JpaRepository<Set, Long> {
    fun findByLiftId(liftId: Long): List<Set>
    fun findByWorkoutExerciseId(workoutExerciseId: Long): List<Set>
}
