package com.jorge.gymtracker.auth.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jorge.gymtracker.auth.AuthScreensViewModel
import com.jorge.gymtracker.auth.nav.Routes

@Composable
fun LoginScreen(nav: NavHostController) {
    val vm: AuthScreensViewModel = viewModel()
    val state = vm.login.collectAsState().value
    var showPass by remember { mutableStateOf(false) }

    AuthScaffold {
        // === TÍTULO CENTRADO Y DORADO ===
        Text(
            "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary, // dorado
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { vm.updateLogin(email = it) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { vm.updateLogin(pass = it) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    if (showPass) "Ocultar" else "Ver",
                    modifier = Modifier
                        .clickable { showPass = !showPass }
                        .padding(8.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(18.dp))

        // Botón principal (dorado + negro para contraste)
        Button(
            onClick = { vm.doLogin { nav.navigate(Routes.HOME) { popUpTo(0) } } },
            enabled = !state.loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.large
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Entrar", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(10.dp))

        TextButton(
            onClick = { nav.navigate(Routes.RESET) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("¿Has olvidado la contraseña?") }

        Spacer(Modifier.height(6.dp))

        TextButton(
            onClick = { nav.navigate(Routes.REGISTER) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("Crear cuenta nueva") }
    }
}
