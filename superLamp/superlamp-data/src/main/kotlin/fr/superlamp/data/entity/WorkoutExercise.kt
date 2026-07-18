package fr.superlamp.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "workout_exercise")
data class WorkoutExercise(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    val workout: Workout,
    
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    val exercise: Exercise,
    
    @Column(nullable = false)
    val order: Int,
    
    @Column(name = "planned_sets", nullable = false)
    val plannedSets: Int,
    
    @Column(name = "planned_reps")
    val plannedReps: Int? = null,
    
    @Column(name = "rest_time_seconds")
    val restTimeSeconds: Int? = null,
    
    @OneToMany(mappedBy = "workoutExercise", cascade = [CascadeType.ALL], orphanRemoval = true)
    val sets: MutableSet<Set> = mutableSetOf()
)
