package fr.superlamp.mobile.core

import java.time.DateTimeException
import java.time.LocalDate

/**
 * Port de `fr.superlamp.core.service.ParserService` (backend).
 *
 * Différences avec le backend :
 * - la date « jj/mm » est complétée avec l'année courante (le backend appelle
 *   `LocalDate.parse("12/05", "dd/MM")`, qui échoue faute d'année) ;
 * - une date dans le futur est ramenée à l'année précédente ;
 * - la date est optionnelle : si le texte commence directement par un exercice,
 *   la séance est datée d'aujourd'hui ;
 * - une date invalide lève une [IllegalArgumentException] avec un message lisible.
 */
class ParserService(private val today: () -> LocalDate = { LocalDate.now() }) {

    fun parse(input: String): ParsedWorkout {
        val parts = input.split(":", limit = 2)
        val head = parts[0].trim()

        val date: LocalDate
        val body: String
        when {
            DATE_REGEX.matches(head) -> {
                date = resolveDate(head)
                body = parts.getOrElse(1) { "" }
            }
            head.isEmpty() -> {
                date = today()
                body = parts.getOrElse(1) { "" }
            }
            else -> {
                date = today()
                body = input
            }
        }

        val exercises = body.lines().mapNotNull { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@mapNotNull null
            val exerciseParts = trimmed.split(":", limit = 2)
            val name = exerciseParts.getOrNull(0)?.trim().orEmpty()
            val setsPart = exerciseParts.getOrNull(1).orEmpty().trim()
            val sets = if (setsPart.isEmpty()) emptyList() else setsPart.split(Regex("\\s+"))
                .mapNotNull { setStr ->
                    val repWeight = setStr.split("*")
                    val reps = repWeight.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
                    val weight = repWeight.getOrNull(1)?.replace(",", ".")?.toDoubleOrNull()
                    ParsedSet(reps, weight)
                }
            ParsedExercise(name, sets)
        }

        return ParsedWorkout(date, exercises)
    }

    private fun resolveDate(text: String): LocalDate {
        val match = DATE_REGEX.matchEntire(text) ?: throw IllegalArgumentException("Date invalide : $text")
        val (day, month, year) = match.destructured
        val now = today()
        return try {
            if (year.isNotEmpty()) {
                val fullYear = year.toInt().let { if (it < 100) 2000 + it else it }
                LocalDate.of(fullYear, month.toInt(), day.toInt())
            } else {
                val candidate = LocalDate.of(now.year, month.toInt(), day.toInt())
                if (candidate.isAfter(now)) candidate.minusYears(1) else candidate
            }
        } catch (e: DateTimeException) {
            throw IllegalArgumentException("Date invalide : $text")
        }
    }

    private companion object {
        val DATE_REGEX = Regex("""(\d{1,2})/(\d{1,2})(?:/(\d{4}|\d{2}))?""")
    }
}
