package com.jorge.gymtracker.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jorge.gymtracker.auth.AuthScreensViewModel

@Composable
fun ResetPasswordScreen(nav: NavHostController) {
    val vm: AuthScreensViewModel = viewModel()
    val state by vm.reset.collectAsState()
    var sent by remember { mutableStateOf(false) }

    AuthScaffold {
        Text("Restablecer contraseña", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { vm.updateReset(email = it) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (sent) {
            Spacer(Modifier.height(8.dp))
            Text("Te hemos enviado un correo para restablecer la contraseña.")
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.doReset { sent = true } },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.loading) CircularProgressIndicator(strokeWidth = 2.dp)
            else Text("Enviar correo")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { nav.popBackStack() }) { Text("Volver") }
    }
}
