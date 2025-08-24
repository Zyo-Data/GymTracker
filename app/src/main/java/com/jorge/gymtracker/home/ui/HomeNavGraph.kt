package com.jorge.gymtracker.home.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jorge.gymtracker.auth.nav.Routes
import com.jorge.gymtracker.ui.theme.HomeScreen

fun NavGraphBuilder.homeGraph(nav: NavHostController) {
    composable(Routes.HOME) { HomeEntry(rootNav = nav) }
}

@Composable
fun HomeEntry(rootNav: NavHostController) {
    HomeScreen(rootNav = rootNav) // Tu pantalla principal (Workout/BottomBar) con menú de usuario
}
