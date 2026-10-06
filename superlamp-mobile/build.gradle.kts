plugins {
    alias(libs.plugins.android.application) apply false
    // AGP 9 compile le Kotlin lui-même ; déclarer le plugin ici fixe seulement la version de Kotlin.
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
