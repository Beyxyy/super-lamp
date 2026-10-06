package fr.superlamp.mobile.core

import java.time.LocalDate

/**
 * Structure représentant un workout parsé depuis un texte (ex : bloc-notes).
 * Reprise de `fr.superlamp.core.model.WorkoutStruct` côté backend.
 *
 * Exemple de format :
 * 27/06:
 * Élévation poulie : 5*30 5*30 15*20
 * Reverse fly : 8*2 7*2 6*2
 */
data class ParsedWorkout(
    val date: LocalDate,
    val exercises: List<ParsedExercise>,
)

/** Un exercice avec ses séries. */
data class ParsedExercise(
    val name: String,
    val sets: List<ParsedSet>,
)

/**
 * Une série d'un exercice.
 * @param reps Nombre de répétitions
 * @param weight Poids en kg (null pour les exercices au poids du corps)
 */
data class ParsedSet(
    val reps: Int,
    val weight: Double?,
)
