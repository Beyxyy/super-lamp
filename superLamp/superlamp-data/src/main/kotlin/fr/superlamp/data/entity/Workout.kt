package fr.superlamp.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "workout")
data class WorkoutEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne
    @JoinColumn(name = "split_id", nullable = false)
    val split: SplitEntity,
    
    @Column(nullable = false)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(name = "workout_order", nullable = false)
    val order: Int,
    
    @OneToMany(mappedBy = "workout", cascade = [CascadeType.ALL], orphanRemoval = true)
    val workoutExercises: MutableSet<WorkoutExerciseEntity> = mutableSetOf(),
    
    @OneToMany(mappedBy = "workout", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lifts: MutableSet<LiftEntity> = mutableSetOf()
)
