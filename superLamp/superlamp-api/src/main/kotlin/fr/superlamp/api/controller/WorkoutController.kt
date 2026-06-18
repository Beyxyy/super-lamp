package fr.superlamp.api.controller

import fr.superlamp.data.entity.Workout
import fr.superlamp.data.service.WorkoutService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/workouts")
// class WorkoutController(private val workoutService: WorkoutService) {
class WorkoutController() {

    // @GetMapping
    // fun getAllWorkouts(): List<Workout> {
    //     return workoutService.getAllWorkouts()
    // }

    // @GetMapping("/{id}")
    // fun getWorkoutById(@PathVariable id: Long): Workout? {
    //     return workoutService.getWorkoutById(id)
    // }

    // @PostMapping
    // fun createWorkout(@RequestBody workout: Workout): Workout {
    //     return workoutService.createWorkout(workout)
    // }

    // @PutMapping("/{id}")
    // fun updateWorkout(@PathVariable id: Long, @RequestBody workout: Workout): Workout? {
    //     return workoutService.updateWorkout(id, workout)
    // }

    // @DeleteMapping("/{id}")
    // fun deleteWorkout(@PathVariable id: Long) {
    //     workoutService.deleteWorkout(id)
    // }
}
