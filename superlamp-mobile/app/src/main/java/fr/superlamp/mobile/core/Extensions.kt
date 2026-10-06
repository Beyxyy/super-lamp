package fr.superlamp.mobile.core

/** Texte nettoyé, ou null s'il est vide. */
fun String?.cleanOrNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

/**
 * Déplace l'élément [index] de [direction] cases (-1 = monter, +1 = descendre).
 * Retourne null si le déplacement sort de la liste.
 */
fun <T> List<T>.moved(index: Int, direction: Int): List<T>? {
    val target = index + direction
    if (index !in indices || target !in indices) return null
    return toMutableList().apply { add(target, removeAt(index)) }
}
