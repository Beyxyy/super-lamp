package fr.superlamp.data.repository

import fr.superlamp.data.entity.Workout
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutRepository : JpaRepository<Workout, Long>
