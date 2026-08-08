package fr.superlamp.data.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "lift")
data class LiftEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    
    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    val workout: WorkoutEntity,
    
    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,
    
    @Column(name = "end_time")
    val endTime: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "lift", cascade = [CascadeType.ALL], orphanRemoval = true)
    val sets: MutableSet<SetEntity> = mutableSetOf()
)
