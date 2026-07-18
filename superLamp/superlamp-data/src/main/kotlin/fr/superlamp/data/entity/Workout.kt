package fr.superlamp.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "workout")
data class Workout(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne
    @JoinColumn(name = "split_id", nullable = false)
    val split: Split,
    
    @Column(nullable = false)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(nullable = false)
    val order: Int,
    
    @OneToMany(mappedBy = "workout", cascade = [CascadeType.ALL], orphanRemoval = true)
    val workoutExercises: MutableSet<WorkoutExercise> = mutableSetOf(),
    
    @OneToMany(mappedBy = "workout", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lifts: MutableSet<Lift> = mutableSetOf()
)
