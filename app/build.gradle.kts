plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")   // Requerido con Kotlin 2.x para Compose
    id("org.jetbrains.kotlin.kapt")            // Para Room (annotation processor)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.jorge.gymtracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jorge.gymtracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    // Con Kotlin 2.x + plugin compose, NO usar composeOptions{kotlinCompilerExtensionVersion = ...}

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    // --- Base con tu catálogo (libs.*) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("com.google.code.gson:gson:2.11.0")


    // Compose BOM + UI + Material3 desde tu catálogo
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // NECESARIO para estilos XML Theme.Material3.* y attrs (colorPrimary, etc.)
    implementation("com.google.android.material:material:1.12.0")

    // Navegación Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")
    // Splash API moderna
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Firebase Auth (usa BOM)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-auth-ktx")

    // Room (DB)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    // Si prefieres annotationProcessor en vez de kapt (Java), añade:
    // annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Coroutines (ViewModels/repos)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // --- Tests que ya tenías ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
