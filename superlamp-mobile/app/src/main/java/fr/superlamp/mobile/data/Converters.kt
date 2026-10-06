package fr.superlamp.mobile.data

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Format à largeur fixe : les dates restent triables comme du texte en SQL. */
private val DB_DATE_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

class Converters {
    @TypeConverter
    fun fromDateTime(value: LocalDateTime?): String? = value?.format(DB_DATE_TIME)

    @TypeConverter
    fun toDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it, DB_DATE_TIME) }
}
