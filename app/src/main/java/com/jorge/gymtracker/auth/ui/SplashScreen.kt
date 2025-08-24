package com.jorge.gymtracker.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
            SessionState.LoggedIn -> nav.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
            SessionState.LoggedOut -> nav.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } }
            SessionState.Loading -> Unit
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher), // tu logo
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )
    }
}
