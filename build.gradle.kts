// build.gradle.kts (Project root)

plugins {
    // vacío a propósito: las versiones ya están en settings.gradle.kts
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
