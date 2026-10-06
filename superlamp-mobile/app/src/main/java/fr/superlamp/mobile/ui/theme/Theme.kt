package fr.superlamp.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import fr.superlamp.mobile.R

/** Police du logo, reprise du front Angular (assets/font/Corinthia). */
val Corinthia = FontFamily(Font(R.font.corinthia_bold, FontWeight.Bold))

private val LightColors = lightColorScheme(
    primary = Color(0xFF9A4600),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBC8),
    onPrimaryContainer = Color(0xFF321200),
    secondary = Color(0xFF765848),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBC8),
    onSecondaryContainer = Color(0xFF2B160A),
    tertiary = Color(0xFF636032),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEAE5AA),
    onTertiaryContainer = Color(0xFF1E1C00),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF221A15),
    surface = Color(0xFFFFF8F5),
    onSurface = Color(0xFF221A15),
    surfaceVariant = Color(0xFFF4DED4),
    onSurfaceVariant = Color(0xFF52443C),
    outline = Color(0xFF85746B),
    outlineVariant = Color(0xFFD7C3B8),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB68B),
    onPrimary = Color(0xFF532200),
    primaryContainer = Color(0xFF763300),
    onPrimaryContainer = Color(0xFFFFDBC8),
    secondary = Color(0xFFE6BEAB),
    onSecondary = Color(0xFF432B1D),
    secondaryContainer = Color(0xFF5C4032),
    onSecondaryContainer = Color(0xFFFFDBC8),
    tertiary = Color(0xFFCDC990),
    onTertiary = Color(0xFF343107),
    tertiaryContainer = Color(0xFF4B481D),
    onTertiaryContainer = Color(0xFFEAE5AA),
    background = Color(0xFF1A120D),
    onBackground = Color(0xFFF0DFD7),
    surface = Color(0xFF1A120D),
    onSurface = Color(0xFFF0DFD7),
    surfaceVariant = Color(0xFF52443C),
    onSurfaceVariant = Color(0xFFD7C3B8),
    outline = Color(0xFFA08D83),
    outlineVariant = Color(0xFF52443C),
)

@Composable
fun SuperLampTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
