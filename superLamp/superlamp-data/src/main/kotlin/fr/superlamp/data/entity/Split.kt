package fr.superlamp.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "split")
data class SplitEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @OneToMany(mappedBy = "split", cascade = [CascadeType.ALL], orphanRemoval = true)
    val workouts: MutableSet<WorkoutEntity> = mutableSetOf()
)