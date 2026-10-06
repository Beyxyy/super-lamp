package fr.superlamp.mobile.ui

import fr.superlamp.mobile.data.entity.LiftSet
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FR: Locale = Locale.FRENCH
private val shortDayFormatter = DateTimeFormatter.ofPattern("EEE d MMM", FR)
private val longDayFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", FR)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", FR)
private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", FR)

private fun String.capitalized(): String = replaceFirstChar { it.titlecase(FR) }

fun LocalDateTime.shortDay(): String = format(shortDayFormatter).capitalized()

fun LocalDate.longDay(): String = format(longDayFormatter).capitalized()

fun LocalDateTime.time(): String = format(timeFormatter)

fun YearMonth.label(): String = format(monthFormatter).capitalized()

/** 62.5 -> « 62,5 », 100.0 -> « 100 ». */
fun formatWeightNumber(kg: Double): String =
    BigDecimal.valueOf(kg).stripTrailingZeros().toPlainString().replace('.', ',')

fun formatWeight(kg: Double?): String = if (kg == null) "—" else "${formatWeightNumber(kg)} kg"

fun formatSet(reps: Int, kg: Double?): String =
    if (kg == null) "$reps reps" else "$reps × ${formatWeightNumber(kg)} kg"

/** « 8×60 · 8×60 · 6×60 », même notation que l'import texte. */
fun formatSetsCompact(sets: List<LiftSet>): String = sets.joinToString(" · ") { set ->
    set.weightKg?.let { "${set.reps}×${formatWeightNumber(it)}" } ?: "${set.reps}"
}

fun formatRest(seconds: Int): String =
    if (seconds >= 60) "%d:%02d".format(seconds / 60, seconds % 60) else "$seconds s"

fun formatDuration(duration: Duration): String {
    val minutes = duration.toMinutes().coerceAtLeast(0)
    return if (minutes >= 60) "${minutes / 60} h %02d".format(minutes % 60) else "$minutes min"
}

/** Chronomètre « 12:34 » ou « 1:02:03 ». */
fun formatClock(duration: Duration): String {
    val total = duration.seconds.coerceAtLeast(0)
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds) else "%02d:%02d".format(minutes, seconds)
}

fun formatVolume(kg: Double): String = String.format(FR, "%,.0f kg", kg)

fun parseWeight(text: String): Double? = text.trim().replace(',', '.').toDoubleOrNull()

fun plural(count: Int, word: String): String = "$count $word" + if (count > 1) "s" else ""

fun plannedLabel(sets: Int, reps: Int?, restSeconds: Int?): String = buildString {
    append(plural(sets, "série"))
    if (reps != null) append(" × $reps reps")
    if (restSeconds != null && restSeconds > 0) append(" · repos ${formatRest(restSeconds)}")
}
