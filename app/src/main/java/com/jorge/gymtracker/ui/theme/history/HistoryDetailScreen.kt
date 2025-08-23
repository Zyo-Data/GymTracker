package com.jorge.gymtracker.ui.theme.history

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jorge.gymtracker.data.repository.WorkoutRepository
import com.jorge.gymtracker.data.entity.SessionWithSets
import com.jorge.gymtracker.ui.theme.components.GymTopBar

@Composable
fun HistoryDetailScreen(
    sessionId: Long,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember { WorkoutRepository(ctx) }
    var detail by remember { mutableStateOf<SessionWithSets?>(null) }

    LaunchedEffect(sessionId) {
        detail = repo.getSessionWithSets(sessionId)
    }

    Scaffold(
        topBar = { GymTopBar("Detalle de sesión", canNavigateBack = true, onBack = onBack) }
    ) { inner ->
        detail?.let { data ->
            LazyColumn(Modifier.padding(inner)) {
                items(data.sets) { set ->
                    ListItem(
                        headlineContent = { Text(set.exerciseName) },
                        supportingContent = { Text("${set.reps} × ${set.weight} kg") }
                    )
                    Divider()
                }
            }
        } ?: Text("Cargando...", Modifier.padding(inner))
    }
}
