package com.jorge.gymtracker.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jorge.gymtracker.R
import com.jorge.gymtracker.auth.AuthViewModel
import com.jorge.gymtracker.auth.SessionState
import com.jorge.gymtracker.auth.nav.Routes

@Composable
fun SplashScreen(nav: NavHostController) {
    val vm: AuthViewModel = viewModel()
    val state by vm.session.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            SessionState.LoggedIn -> nav.navigate(Routes.HOME) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
            SessionState.LoggedOut -> nav.navigate(Routes.LOGIN) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
            SessionState.Loading -> Unit
        }
    }

    // Muestra tu fondo + logo mientras resolvemos sesión
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.gym_background),
                contentScale = ContentScale.Crop
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo"
        )
    }
}
