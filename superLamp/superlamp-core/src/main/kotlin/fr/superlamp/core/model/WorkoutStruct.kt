package fr.superlamp.core.model

import java.time.LocalDate

/**
 * Structure pour représenter un workout parsé depuis un texte (ex: bloc note)
 * Exemple de format:
 * 27/06:
 * Élévation poulie : 5*30 5*30 15*20
 * Reverse fly : 8*2 7*2 6*2
 */
data class ParsedWorkout(
    val date: LocalDate,
    val exercises: List<ParsedExercise>
)

/**
 * Représente un exercice avec ses séries
 */
data class ParsedExercise(
    val name: String,
    val sets: List<ParsedSet>
)

/**
 * Représente une série d'un exercice
 * @param reps Nombre de répétitions
 * @param weight Poids en kg (peut être null pour les exercices au poids de corps)
 */
data class ParsedSet(
    val reps: Int,
    val weight: Double?
)
