package fr.superlamp.api.controller

import fr.superlamp.data.entity.Workout
import fr.superlamp.data.service.WorkoutService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/workouts")
class WorkoutController(private val workoutService: WorkoutService) {

    @GetMapping
    fun getAllWorkouts(): List<Workout> {
        return workoutService.getAllWorkouts()
    }

    @GetMapping("/{id}")
    fun getWorkoutById(@PathVariable id: Long): Workout? {
        return workoutService.getWorkoutById(id)
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun createWorkout(@RequestBody workout: Workout): Workout {
        return workoutService.createWorkout(workout)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    fun updateWorkout(@PathVariable id: Long, @RequestBody workout: Workout): Workout? {
        return workoutService.updateWorkout(id, workout)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteWorkout(@PathVariable id: Long) {
        workoutService.deleteWorkout(id)
    }
}
