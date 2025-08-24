package com.jorge.gymtracker.auth.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jorge.gymtracker.auth.nav.Routes

fun NavGraphBuilder.authGraph(nav: NavHostController) {
    composable(Routes.SPLASH) { SplashScreen(nav) }
    composable(Routes.LOGIN) { LoginScreen(nav) }
    composable(Routes.REGISTER) { RegisterScreen(nav) }
    composable(Routes.RESET) { ResetPasswordScreen(nav) }
}
