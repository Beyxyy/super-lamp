package fr.superlamp.api.controller

import fr.superlamp.api.dto.*
import fr.superlamp.api.mapper.*
import fr.superlamp.core.service.ParserService
import fr.superlamp.core.service.WorkoutCoreService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/workouts")
class WorkoutController(
    private val workoutCoreService: WorkoutCoreService
) {

    @GetMapping
    fun getAllWorkouts(): List<WorkoutResponse> {
        return workoutCoreService.getAllWorkouts().map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getWorkoutById(@PathVariable id: Long): WorkoutResponse {
        return workoutCoreService.getWorkoutById(id).toResponse()
    }

    @GetMapping("/split/{splitId}")
    fun getWorkoutsBySplitId(@PathVariable splitId: Long): List<WorkoutResponse> {
        return workoutCoreService.getWorkoutsBySplitId(splitId).map { it.toResponse() }
    }

    @PostMapping
    fun create(@RequestBody request: WorkoutRequest): WorkoutResponse {
        val workout = workoutCoreService.createWorkout(request.toModel())
        return workout.toResponse()
    }

    @PutMapping("/{id}")
    fun updateWorkout(
        @PathVariable id: Long,
        @RequestBody request: WorkoutRequest
    ): WorkoutResponse {
        val workout = request.toModel().copy(id = id)
        return workoutCoreService.updateWorkout(id, workout).toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteWorkout(@PathVariable id: Long) {
        workoutCoreService.deleteWorkout(id)
    }

    @PostMapping("/fromText")
    fun createFromText(@RequestBody request: WorkoutStructRequest) {
        val parserService = fr.superlamp.core.service.ParserService()
        val parsedWorkout = parserService.parse(request.string)
        // TODO: Convertir ParsedWorkout vers Workout et sauvegarder via workoutCoreService
    }
}
