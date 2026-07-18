package fr.superlamp.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "exercise")
data class Exercise(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(name = "muscle_group")
    val muscleGroup: String? = null,
    
    @OneToMany(mappedBy = "exercise", cascade = [CascadeType.ALL], orphanRemoval = true)
    val workoutExercises: MutableSet<WorkoutExercise> = mutableSetOf()
)
