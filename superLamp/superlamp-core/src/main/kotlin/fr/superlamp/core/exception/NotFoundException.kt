package fr.superlamp.core.exception

/**
 * Exception générique pour les entités non trouvées
 */
open class NotFoundException(message: String) : RuntimeException(message)

class WorkoutNotFoundException(id: Long) : NotFoundException("Workout with id $id not found")
class ExerciseNotFoundException(id: Long) : NotFoundException("Exercise with id $id not found")
class SplitNotFoundException(id: Long) : NotFoundException("Split with id $id not found")
class LiftNotFoundException(id: Long) : NotFoundException("Lift with id $id not found")
class WorkoutExerciseNotFoundException(id: Long) : NotFoundException("WorkoutExercise with id $id not found")
class SetNotFoundException(id: Long) : NotFoundException("Set with id $id not found")
