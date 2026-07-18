package fr.superlamp.data.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "lift_set")
data class Set(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne
    @JoinColumn(name = "lift_id", nullable = false)
    val lift: Lift,
    
    @ManyToOne
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    val workoutExercise: WorkoutExercise,
    
    @Column(name = "set_order", nullable = false)
    val order: Int,
    
    @Column(nullable = false)
    val reps: Int,
    
    @Column(name = "weight_kg")
    val weightKg: Float? = null,
    
    @Column(name = "rest_time_seconds")
    val restTimeSeconds: Int? = null,
    
    @Column
    val notes: String? = null,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
