package fr.superlamp.mobile.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

/** Port des tests de `ParserServiceTest` (backend), avec une date du jour fixe. */
class ParserServiceTest {

    private val today = LocalDate.of(2026, 10, 6)
    private val parser = ParserService { today }

    // --- Structure ---

    @Test
    fun `parse date and single exercise with single set`() {
        val result = parser.parse("12/05:Squat: 5*100")

        assertEquals(LocalDate.of(2026, 5, 12), result.date)
        assertEquals(1, result.exercises.size)
        assertEquals("Squat", result.exercises[0].name)
        assertEquals(1, result.exercises[0].sets.size)
        assertEquals(5, result.exercises[0].sets[0].reps)
        assertEquals(100.0, result.exercises[0].sets[0].weight)
    }

    @Test
    fun `parse date and single exercise with multiple sets`() {
        val result = parser.parse("12/05:Squat: 5*100 8*90 10*80")

        val sets = result.exercises[0].sets
        assertEquals(listOf(5, 8, 10), sets.map { it.reps })
        assertEquals(listOf(100.0, 90.0, 80.0), sets.map { it.weight })
    }

    @Test
    fun `parse multiple exercises`() {
        val result = parser.parse("12/05:Squat: 5*100\nBench Press: 3*80")

        assertEquals(listOf("Squat", "Bench Press"), result.exercises.map { it.name })
    }

    // --- Dates ---

    @Test
    fun `use current date when no date provided`() {
        val result = parser.parse(":Squat: 5*100")

        assertEquals(today, result.date)
        assertEquals(1, result.exercises.size)
    }

    @Test
    fun `use current date when input is empty`() {
        val result = parser.parse("")

        assertEquals(today, result.date)
        assertEquals(0, result.exercises.size)
    }

    @Test
    fun `text without date starts directly with exercises`() {
        val result = parser.parse("Squat: 5*100\nBench Press: 3*80")

        assertEquals(today, result.date)
        assertEquals(listOf("Squat", "Bench Press"), result.exercises.map { it.name })
    }

    @Test
    fun `date in the future refers to previous year`() {
        val result = parser.parse("25/12:Squat: 5*100")

        assertEquals(LocalDate.of(2025, 12, 25), result.date)
    }

    @Test
    fun `explicit year is kept`() {
        assertEquals(LocalDate.of(2025, 5, 12), parser.parse("12/05/25:Squat: 5").date)
        assertEquals(LocalDate.of(2024, 5, 12), parser.parse("12/05/2024:Squat: 5").date)
    }

    @Test
    fun `throw exception for impossible date`() {
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse("31/02:Squat: 5*100")
        }
    }

    // --- Séries ---

    @Test
    fun `parse decimal weight with comma`() {
        assertEquals(75.5, parser.parse("12/05:Squat: 5*75,5").exercises[0].sets[0].weight)
    }

    @Test
    fun `parse decimal weight with dot`() {
        assertEquals(75.5, parser.parse("12/05:Squat: 5*75.5").exercises[0].sets[0].weight)
    }

    @Test
    fun `parse set without weight`() {
        val set = parser.parse("12/05:Squat: 5*").exercises[0].sets[0]

        assertEquals(5, set.reps)
        assertNull(set.weight)
    }

    @Test
    fun `parse set with only reps`() {
        val set = parser.parse("12/05:Squat: 5").exercises[0].sets[0]

        assertEquals(5, set.reps)
        assertNull(set.weight)
    }

    @Test
    fun `skip invalid set format`() {
        val sets = parser.parse("12/05:Squat: 5*100 invalid 8*90").exercises[0].sets

        assertEquals(listOf(5, 8), sets.map { it.reps })
        assertEquals(listOf(100.0, 90.0), sets.map { it.weight })
    }

    // --- Cas limites ---

    @Test
    fun `skip empty lines`() {
        val result = parser.parse("12/05:\n\nSquat: 5*100\n\nBench Press: 3*80\n")

        assertEquals(listOf("Squat", "Bench Press"), result.exercises.map { it.name })
    }

    @Test
    fun `handle exercise with no sets`() {
        val result = parser.parse("12/05:Squat:")

        assertEquals(1, result.exercises.size)
        assertEquals("Squat", result.exercises[0].name)
        assertEquals(0, result.exercises[0].sets.size)
    }

    @Test
    fun `handle exercise with no name`() {
        val result = parser.parse("12/05:: 5*100")

        assertEquals("", result.exercises[0].name)
        assertEquals(1, result.exercises[0].sets.size)
    }

    @Test
    fun `return empty exercises list for input with only date`() {
        val result = parser.parse("12/05:")

        assertEquals(LocalDate.of(2026, 5, 12), result.date)
        assertEquals(0, result.exercises.size)
    }

    @Test
    fun `handle multiple spaces and tabs between sets`() {
        assertEquals(3, parser.parse("12/05:Squat: 5*100    8*90  10*80").exercises[0].sets.size)
        assertEquals(3, parser.parse("12/05:Squat: 5*100\t8*90\t10*80").exercises[0].sets.size)
    }

    // --- Exemples réels ---

    @Test
    fun `parse example from documentation`() {
        val result = parser.parse(
            """27/06:
Élévation poulie : 5*30 5*30 15*20
Reverse fly : 8*2 7*2 6*2"""
        )

        assertEquals(LocalDate.of(2026, 6, 27), result.date)
        assertEquals(listOf("Élévation poulie", "Reverse fly"), result.exercises.map { it.name })
        assertEquals(3, result.exercises[0].sets.size)
        assertEquals(30.0, result.exercises[0].sets[0].weight)
        assertEquals(8, result.exercises[1].sets[0].reps)
        assertEquals(2.0, result.exercises[1].sets[0].weight)
    }

    @Test
    fun `parse complex workout with mixed weights`() {
        val result = parser.parse(
            """10/07:
Squat: 5*100 8*90 10*80
Bench Press: 3*80,5 4*75
Deadlift: 5*120"""
        )

        assertEquals(LocalDate.of(2026, 7, 10), result.date)
        assertEquals(3, result.exercises.size)
        assertEquals(80.5, result.exercises[1].sets[0].weight)
    }
}
