package fr.superlamp.core.service

import fr.superlamp.core.model.ParsedExercise
import fr.superlamp.core.model.ParsedSet
import fr.superlamp.core.model.ParsedWorkout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.format.DateTimeParseException

@DisplayName("ParserService Tests")
class ParserServiceTest {

    private val parserService = ParserService()

    @Nested
    @DisplayName("parse() - Basic Structure")
    inner class ParseBasicStructure {

        @Test
        @DisplayName("Should parse date and single exercise with single set")
        fun `parse date and single exercise with single set`() {
            // Given
            val input = "12/05:Squat: 5*100"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.of(2026, 5, 12), result.date)
            assertEquals(1, result.exercises.size)
            assertEquals("Squat", result.exercises[0].name)
            assertEquals(1, result.exercises[0].sets.size)
            assertEquals(5, result.exercises[0].sets[0].reps)
            assertEquals(100.0, result.exercises[0].sets[0].weight)
        }

        @Test
        @DisplayName("Should parse date and single exercise with multiple sets")
        fun `parse date and single exercise with multiple sets`() {
            // Given
            val input = "12/05:Squat: 5*100 8*90 10*80"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.of(2026, 5, 12), result.date)
            assertEquals(1, result.exercises.size)
            assertEquals("Squat", result.exercises[0].name)
            assertEquals(3, result.exercises[0].sets.size)
            assertEquals(5, result.exercises[0].sets[0].reps)
            assertEquals(100.0, result.exercises[0].sets[0].weight)
            assertEquals(8, result.exercises[0].sets[1].reps)
            assertEquals(90.0, result.exercises[0].sets[1].weight)
            assertEquals(10, result.exercises[0].sets[2].reps)
            assertEquals(80.0, result.exercises[0].sets[2].weight)
        }

        @Test
        @DisplayName("Should parse multiple exercises")
        fun `parse multiple exercises`() {
            // Given
            val input = "12/05:Squat: 5*100\nBench Press: 3*80"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.of(2026, 5, 12), result.date)
            assertEquals(2, result.exercises.size)
            assertEquals("Squat", result.exercises[0].name)
            assertEquals(1, result.exercises[0].sets.size)
            assertEquals("Bench Press", result.exercises[1].name)
            assertEquals(1, result.exercises[1].sets.size)
        }
    }

    @Nested
    @DisplayName("parse() - Date Handling")
    inner class ParseDateHandling {

        @Test
        @DisplayName("Should use current date when no date provided")
        fun `use current date when no date provided`() {
            // Given
            val input = ":Squat: 5*100"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.now(), result.date)
            assertEquals(1, result.exercises.size)
        }

        @Test
        @DisplayName("Should use current date when input is empty")
        fun `use current date when input is empty`() {
            // Given
            val input = ""

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.now(), result.date)
            assertEquals(0, result.exercises.size)
        }

        @Test
        @DisplayName("Should throw exception for invalid date format")
        fun `throw exception for invalid date format`() {
            // Given
            val input = "invalid-date:Squat: 5*100"

            // When/Then
            assertThrows<DateTimeParseException> {
                parserService.parse(input)
            }
        }
    }

    @Nested
    @DisplayName("parse() - Set Parsing")
    inner class ParseSetParsing {

        @Test
        @DisplayName("Should parse decimal weight with comma")
        fun `parse decimal weight with comma`() {
            // Given
            val input = "12/05:Squat: 5*75,5"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(75.5, result.exercises[0].sets[0].weight)
        }

        @Test
        @DisplayName("Should parse decimal weight with dot")
        fun `parse decimal weight with dot`() {
            // Given
            val input = "12/05:Squat: 5*75.5"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(75.5, result.exercises[0].sets[0].weight)
        }

        @Test
        @DisplayName("Should parse set without weight (weight is null)")
        fun `parse set without weight`() {
            // Given
            val input = "12/05:Squat: 5*"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(5, result.exercises[0].sets[0].reps)
            assertNull(result.exercises[0].sets[0].weight)
        }

        @Test
        @DisplayName("Should parse set with only reps (no asterisk)")
        fun `parse set with only reps`() {
            // Given
            val input = "12/05:Squat: 5"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(5, result.exercises[0].sets[0].reps)
            assertNull(result.exercises[0].sets[0].weight)
        }

        @Test
        @DisplayName("Should skip invalid set format")
        fun `skip invalid set format`() {
            // Given
            val input = "12/05:Squat: 5*100 invalid 8*90"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(2, result.exercises[0].sets.size)
            assertEquals(5, result.exercises[0].sets[0].reps)
            assertEquals(100.0, result.exercises[0].sets[0].weight)
            assertEquals(8, result.exercises[0].sets[1].reps)
            assertEquals(90.0, result.exercises[0].sets[1].weight)
        }
    }

    @Nested
    @DisplayName("parse() - Edge Cases")
    inner class ParseEdgeCases {

        @Test
        @DisplayName("Should skip empty lines")
        fun `skip empty lines`() {
            // Given
            val input = "12/05:\n\nSquat: 5*100\n\nBench Press: 3*80\n"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(2, result.exercises.size)
            assertEquals("Squat", result.exercises[0].name)
            assertEquals("Bench Press", result.exercises[1].name)
        }

        @Test
        @DisplayName("Should handle exercise with no sets")
        fun `handle exercise with no sets`() {
            // Given
            val input = "12/05:Squat:"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(1, result.exercises.size)
            assertEquals("Squat", result.exercises[0].name)
            assertEquals(0, result.exercises[0].sets.size)
        }

        @Test
        @DisplayName("Should handle exercise with no name")
        fun `handle exercise with no name`() {
            // Given
            val input = "12/05:: 5*100"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(1, result.exercises.size)
            assertEquals("", result.exercises[0].name)
            assertEquals(1, result.exercises[0].sets.size)
        }

        @Test
        @DisplayName("Should return empty exercises list for input with only date")
        fun `return empty exercises list for input with only date`() {
            // Given
            val input = "12/05:"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.of(2026, 5, 12), result.date)
            assertEquals(0, result.exercises.size)
        }

        @Test
        @DisplayName("Should handle multiple spaces in set separators")
        fun `handle multiple spaces in set separators`() {
            // Given
            val input = "12/05:Squat: 5*100    8*90  10*80"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(3, result.exercises[0].sets.size)
        }

        @Test
        @DisplayName("Should handle tab separators in sets")
        fun `handle tab separators in sets`() {
            // Given
            val input = "12/05:Squat: 5*100\t8*90\t10*80"

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(3, result.exercises[0].sets.size)
        }
    }

    @Nested
    @DisplayName("parse() - Real World Examples")
    inner class ParseRealWorldExamples {

        @Test
        @DisplayName("Should parse example from documentation")
        fun `parse example from documentation`() {
            // Given - Format from WorkoutStruct.kt documentation
            val input = """27/06:
Élévation poulie : 5*30 5*30 15*20
Reverse fly : 8*2 7*2 6*2"""

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.of(2026, 6, 27), result.date)
            assertEquals(2, result.exercises.size)

            // First exercise: Élévation poulie
            assertEquals("Élévation poulie", result.exercises[0].name)
            assertEquals(3, result.exercises[0].sets.size)
            assertEquals(5, result.exercises[0].sets[0].reps)
            assertEquals(30.0, result.exercises[0].sets[0].weight)

            // Second exercise: Reverse fly
            assertEquals("Reverse fly", result.exercises[1].name)
            assertEquals(3, result.exercises[1].sets.size)
            assertEquals(8, result.exercises[1].sets[0].reps)
            assertEquals(2.0, result.exercises[1].sets[0].weight)
        }

        @Test
        @DisplayName("Should parse complex workout with mixed weights")
        fun `parse complex workout`() {
            // Given
            val input = """10/07:
Squat: 5*100 8*90 10*80
Bench Press: 3*80,5 4*75
Deadlift: 5*120"""

            // When
            val result = parserService.parse(input)

            // Then
            assertEquals(LocalDate.of(2026, 7, 10), result.date)
            assertEquals(3, result.exercises.size)

            // Bench Press with decimal weight
            assertEquals("Bench Press", result.exercises[1].name)
            assertEquals(80.5, result.exercises[1].sets[0].weight)
        }
    }
}
