package com.jorge.gymtracker.ui.theme.navigation

sealed class Routes(val route: String, val title: String) {
    object Home : Routes("home", "Inicio")
    object Routines : Routes("routines", "Rutinas")
    object Settings : Routes("settings", "Ajustes")

    // ➕ nuevas rutas de acción
    object Workout : Routes("workout", "Entrenamiento")
    object CreateRoutine : Routes("create_routine", "Nueva rutina")
}


