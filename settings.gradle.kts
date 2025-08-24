pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // 🔒 Versiones alineadas con Gradle 8.13 y Kotlin 2.0.x
    plugins {
        id("com.android.application") version "8.12.1"               // AGP
        id("org.jetbrains.kotlin.android") version "2.0.20"         // Kotlin
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"  // Compose plugin requerido
        id("com.google.gms.google-services") version "4.4.3"
        id("org.jetbrains.kotlin.kapt") version "2.0.20"// Firebase
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GymTracker"
include(":app")
