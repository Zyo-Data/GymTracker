package com.jorge.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jorge.gymtracker.ui.theme.GymTrackerTheme
import com.jorge.gymtracker.ui.theme.HomeScreen   // importa tu pantalla principal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymTrackerTheme {
                HomeScreen()   // aquí va tu navegación
            }
        }
    }
}
