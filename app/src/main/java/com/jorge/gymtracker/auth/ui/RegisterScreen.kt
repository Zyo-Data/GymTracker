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
fun RegisterScreen(nav: NavHostController) {
    val vm: AuthScreensViewModel = viewModel()
    val state = vm.register.collectAsState().value
    var showPass by remember { mutableStateOf(false) }

    AuthScaffold {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { vm.updateRegister(email = it) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { vm.updateRegister(pass = it) },
            label = { Text("Contraseña (mín. 6)") },
            singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    if (showPass) "Ocultar" else "Ver",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showPass = !showPass },
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
            onClick = { vm.doRegister { nav.navigate(Routes.HOME) { popUpTo(0) } } },
            enabled = !state.loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (state.loading) CircularProgressIndicator(strokeWidth = 2.dp)
            else Text("Crear y entrar")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { nav.popBackStack() }) { Text("Volver") }
    }
}
