package fr.superlamp.core.service

import fr.superlamp.core.model.ParsedExercise
import fr.superlamp.core.model.ParsedSet
import fr.superlamp.core.model.ParsedWorkout
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ParserService {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
    
    fun parse(input: String): ParsedWorkout {
        val parts = input.split(":", limit = 2)
        val dateStr = parts.getOrNull(0)?.trim().orEmpty()
        val exercisesPart = parts.getOrNull(1).orEmpty()

        val date = if (dateStr.isNotEmpty()) {
            LocalDate.parse(dateStr, dateFormatter)
        } else {
            LocalDate.now()
        }

        val exercises = exercisesPart.lines()
            .mapNotNull { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty()) return@mapNotNull null
                val exerciseParts = trimmed.split(":", limit = 2)
                val name = exerciseParts.getOrNull(0)?.trim().orEmpty()
                val setsPart = exerciseParts.getOrNull(1).orEmpty().trim()
                val sets = if (setsPart.isEmpty()) emptyList() else setsPart.split(Regex("\\s+"))
                    .mapNotNull { setStr ->
                        val repWeight = setStr.split("*")
                        val reps = repWeight.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
                        val weightStr = repWeight.getOrNull(1)?.replace(",", ".")
                        val weight = weightStr?.toDoubleOrNull()
                        ParsedSet(reps, weight)
                    }
                ParsedExercise(name, sets)
            }

        return ParsedWorkout(date, exercises)
    }
}

