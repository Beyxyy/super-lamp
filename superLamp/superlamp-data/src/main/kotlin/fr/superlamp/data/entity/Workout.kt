package fr.superlamp.data.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "workouts")
class Workout(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,
    
    @Column(name = "end_time")
    val endTime: LocalDateTime? = null,
    
    @Column(name = "duration_seconds")
    val durationSeconds: Long? = null,
    
    @Column(name = "calories_burned")
    val caloriesBurned: Int? = null
)
