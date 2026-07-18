package fr.superlamp.api.controller

import fr.superlamp.data.entity.Workout
import fr.superlamp.data.service.WorkoutService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/users")
class UserController(private val workoutService: UserService) {


    // @GetMapping
    // fun getAll(): List<Workout> {
    //     return workoutService.getAllWorkouts()
    // }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): Workout? {
        return workoutService.getWorkoutById(id)
    }

    // @PostMapping
    // fun create(@RequestBody workout: Workout): Workout {
    //     return workoutService.createWorkout(workout)
    // }

    // @PutMapping("/{id}")
    // fun update(@PathVariable id: Long, @RequestBody workout: Workout): Workout? {
    //     return workoutService.updateWorkout(id, workout)
    // }

    // @DeleteMapping("/{id}")
    // fun deleteWorkout(@PathVariable id: Long) {
    //     workoutService.deleteWorkout(id)
    // }
}
