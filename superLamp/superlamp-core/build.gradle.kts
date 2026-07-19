plugins {
    kotlin("jvm") version "2.2.21"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.mockk:mockk:1.13.10")          // Mocks Kotlin
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0") // JUnit 5
}
