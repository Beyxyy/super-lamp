package fr.superlamp.data.repository

import fr.superlamp.data.entity.WorkoutEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutRepository : JpaRepository<WorkoutEntity, Long> {
    fun findBySplitId(splitId: Long): List<WorkoutEntity>
}
