package fr.superlamp.api.exception

import fr.superlamp.api.dto.ErrorResponse
import fr.superlamp.core.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Instant

/**
 * Gestionnaire global des exceptions pour transformer les erreurs métier
 * en réponses HTTP structurées
 */
@ControllerAdvice
class GlobalExceptionHandler {

    // 404 - Not Found (WorkoutNotFound, ExerciseNotFound, etc.)
    @ExceptionHandler(
        NotFoundException::class,
        WorkoutNotFoundException::class,
        ExerciseNotFoundException::class,
        SplitNotFoundException::class,
        LiftNotFoundException::class,
        WorkoutExerciseNotFoundException::class,
        SetNotFoundException::class
    )
    fun handleNotFound(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "NOT_FOUND",
            message = ex.message ?: "Resource not found",
            timestamp = Instant.now(),
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    // 400 - Bad Request (validation métier)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleValidation(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "VALIDATION_ERROR",
            message = ex.message ?: "Validation failed",
            timestamp = Instant.now(),
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    // 400 - Bad Request (autres exceptions de validation)
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "BAD_REQUEST",
            message = ex.message ?: "Bad request",
            timestamp = Instant.now(),
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    // 500 - Internal Server Error (fallback pour toutes les autres exceptions)
    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "INTERNAL_ERROR",
            message = ex.message ?: "An unexpected error occurred",
            timestamp = Instant.now(),
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
