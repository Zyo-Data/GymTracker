package com.jorge.gymtracker.auth.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jorge.gymtracker.auth.AuthScreensViewModel
import com.jorge.gymtracker.auth.nav.Routes

@Composable
fun LoginScreen(nav: NavHostController) {
    val vm: AuthScreensViewModel = viewModel()
    val state = vm.login.collectAsState().value   // ✅ así recogemos el state

    AuthScaffold {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { vm.updateLogin(email = it) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        var showPass by remember { mutableStateOf(false) }
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

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.doLogin { nav.navigate(Routes.HOME) { popUpTo(0) } } },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.loading) CircularProgressIndicator(strokeWidth = 2.dp)
            else Text("Entrar")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { nav.navigate(Routes.RESET) }) { Text("¿Has olvidado la contraseña?") }

        Spacer(Modifier.height(4.dp))
        TextButton(onClick = { nav.navigate(Routes.REGISTER) }) { Text("Crear cuenta nueva") }
    }
}
