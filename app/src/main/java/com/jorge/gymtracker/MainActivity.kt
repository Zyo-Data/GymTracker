package com.jorge.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jorge.gymtracker.ui.theme.GymTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12+: instala splash del sistema (negro + logo), luego Compose mostrará el fondo
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            GymTrackerTheme {
                GymTrackerApp()
            }
        }
    }
}
