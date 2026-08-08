package fr.superlamp.api.controller

import fr.superlamp.api.dto.WorkoutExerciseRequest
import fr.superlamp.api.dto.WorkoutExerciseResponse
import fr.superlamp.api.mapper.toModel
import fr.superlamp.api.mapper.toResponse
import fr.superlamp.core.service.WorkoutExerciseCoreService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/workoutExercises")
class WorkoutExerciseController(
    private val workoutExerciseCoreService: WorkoutExerciseCoreService
) {

    @GetMapping
    fun getAllWorkoutExercises(): List<WorkoutExerciseResponse> {
        return workoutExerciseCoreService.getAllWorkoutExercises().map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getWorkoutExerciseById(@PathVariable id: Long): WorkoutExerciseResponse {
        return workoutExerciseCoreService.getWorkoutExerciseById(id).toResponse()
    }

    @GetMapping("/workout/{workoutId}")
    fun getWorkoutExercisesByWorkoutId(@PathVariable workoutId: Long): List<WorkoutExerciseResponse> {
        return workoutExerciseCoreService.getWorkoutExercisesByWorkoutId(workoutId).map { it.toResponse() }
    }

    @PostMapping
    fun create(@RequestBody request: WorkoutExerciseRequest): WorkoutExerciseResponse {
        val workoutExercise = workoutExerciseCoreService.createWorkoutExercise(request.toModel())
        return workoutExercise.toResponse()
    }

    @PutMapping("/{id}")
    fun updateWorkoutExercise(
        @PathVariable id: Long,
        @RequestBody request: WorkoutExerciseRequest
    ): WorkoutExerciseResponse {
        val workoutExercise = request.toModel().copy(id = id)
        return workoutExerciseCoreService.updateWorkoutExercise(id, workoutExercise).toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteWorkoutExercise(@PathVariable id: Long) {
        workoutExerciseCoreService.deleteWorkoutExercise(id)
    }
}
