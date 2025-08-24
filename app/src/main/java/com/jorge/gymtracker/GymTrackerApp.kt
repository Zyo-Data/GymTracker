package com.jorge.gymtracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jorge.gymtracker.auth.nav.Routes
import com.jorge.gymtracker.auth.ui.authGraph
import com.jorge.gymtracker.home.ui.homeGraph

@Composable
fun GymTrackerApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        authGraph(navController)  // Splash, Login, Register, Reset
        homeGraph(navController)  // Home -> tu HomeScreen()
    }
}
