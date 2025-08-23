package com.jorge.gymtracker.ui.theme.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.FitnessCenter

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Routes.Home.route, Routes.Home.title, Icons.Filled.Home),
        BottomNavItem(Routes.Workout.route, "Entrenamiento", Icons.Filled.FitnessCenter),
        BottomNavItem(Routes.Routines.route, Routes.Routines.title, Icons.Filled.SportsGymnastics),
        BottomNavItem(Routes.Settings.route, Routes.Settings.title, Icons.Filled.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val selected = destination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selected,
                onClick = {
                    if (item.route == Routes.Home.route) {
                        // 🔥 Inicio: forzamos volver a Home limpio, sin restaurar estados previos
                        navController.navigate(Routes.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true      // limpiamos todo hasta el startDestination
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false     // evitar que “reviva” Entrenamiento
                        }
                    } else {
                        // Resto de pestañas: comportamiento estándar con restoreState
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
