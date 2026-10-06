package fr.superlamp.mobile.data.repository

import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.Workout
import fr.superlamp.mobile.data.entity.WorkoutWithSplit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class NextWorkoutTest {

    private val push = workout(id = 1, splitId = 1, name = "Push", order = 0)
    private val pull = workout(id = 2, splitId = 1, name = "Pull", order = 1)
    private val legs = workout(id = 3, splitId = 1, name = "Legs", order = 2)
    private val fullBody = workout(id = 4, splitId = 2, name = "Full body", order = 0)
    private val all = listOf(push, pull, legs, fullBody)

    @Test
    fun `no workout returns null`() {
        assertNull(nextWorkout(null, emptyList()))
    }

    @Test
    fun `without history the first workout is proposed`() {
        assertEquals(push, nextWorkout(null, all))
    }

    @Test
    fun `proposes the workout following the last one`() {
        assertEquals(pull, nextWorkout(lift(workoutId = 1), all))
        assertEquals(legs, nextWorkout(lift(workoutId = 2), all))
    }

    @Test
    fun `loops back to the first workout of the split`() {
        assertEquals(push, nextWorkout(lift(workoutId = 3), all))
    }

    @Test
    fun `single workout split proposes itself`() {
        assertEquals(fullBody, nextWorkout(lift(workoutId = 4), all))
    }

    @Test
    fun `deleted workout falls back to the first one`() {
        assertEquals(push, nextWorkout(lift(workoutId = 99), all))
    }

    private fun workout(id: Long, splitId: Long, name: String, order: Int) =
        WorkoutWithSplit(Workout(id = id, splitId = splitId, name = name, order = order), splitName = "Split $splitId")

    private fun lift(workoutId: Long) =
        Lift(id = 1, name = "Séance", workoutId = workoutId, startTime = LocalDateTime.of(2026, 10, 1, 18, 0))
}
