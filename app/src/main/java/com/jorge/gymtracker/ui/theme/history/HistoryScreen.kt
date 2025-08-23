package com.jorge.gymtracker.ui.theme.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jorge.gymtracker.data.entity.WorkoutSessionEntity
import com.jorge.gymtracker.data.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController? = null) {
    val ctx = LocalContext.current
    val repo = remember { WorkoutRepository(ctx) }
    var sessions by remember { mutableStateOf(listOf<WorkoutSessionEntity>()) }
    val scope = rememberCoroutineScope()

    var sessionToDelete by remember { mutableStateOf<WorkoutSessionEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        sessions = repo.getHistoryWithSets().map { it.session }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { navController?.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text("Historial de entrenamientos", style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(Modifier.height(12.dp))

            if (sessions.isEmpty()) {
                Text("No hay sesiones registradas aún.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sessions) { session ->
                        SessionRow(
                            session = session,
                            onClick = { navController?.navigate("sessionDetail/${session.id}") },
                            onDelete = { sessionToDelete = session }
                        )
                    }
                }
            }
        }
    }

    // 🔔 Diálogo de confirmación de borrado
    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Eliminar sesión") },
            text = { Text("¿Estás seguro de que quieres borrar esta sesión de forma permanente? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repo.deleteSession(session.id)
                        sessions = repo.getHistoryWithSets().map { it.session }
                        snackbarHostState.showSnackbar("Sesión eliminada")
                        sessionToDelete = null
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SessionRow(
    session: WorkoutSessionEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        ListItem(
            headlineContent = { Text(session.title) },
            supportingContent = {
                val sdf = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
                Text(sdf.format(Date(session.date)))
            },
            trailingContent = {
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        )
    }
}
