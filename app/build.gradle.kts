plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Para Room (annotation processing)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.jorge.gymtracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jorge.gymtracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
    // Si tu Android Gradle Plugin pide versión del compiler de Compose, descomenta:
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.5.14"
    // }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM + UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.6.8")
    implementation("androidx.compose.material3:material3:1.3.0")


    // Navigation-Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Material Icons Extended (para más iconos)
    implementation("androidx.compose.material:material-icons-extended")

    // Gson (leer JSON de ejercicios)
    implementation("com.google.code.gson:gson:2.11.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // (Opcional) kotlinx-serialization si la usas en otros sitios (sin plugin)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// (Opcional) Si usas KSP en lugar de KAPT, cambia arriba:
// plugins { id("com.google.devtools.ksp") } y aquí: ksp("androidx.room:room-compiler:2.6.1")
