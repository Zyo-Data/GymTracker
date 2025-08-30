package com.jorge.gymtracker.ui.theme.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jorge.gymtracker.data.db.AppDb
import com.jorge.gymtracker.data.entity.SessionWithSets
import com.jorge.gymtracker.data.repository.WorkoutRepository
import com.jorge.gymtracker.domain.PRService
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    sessionId: Long,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val db = remember { AppDb.get(ctx) }
    val prService = remember { PRService(db.workoutDao(), db.personalRecordDao(), db.progressionRuleDao()) }
    val workoutRepo = remember { WorkoutRepository(ctx, prService) }

    var session by remember { mutableStateOf<SessionWithSets?>(null) }

    LaunchedEffect(sessionId) {
        session = workoutRepo.getSessionWithSets(sessionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(session?.session?.title ?: "Detalle de sesión") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val s = session
        if (s == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // 👉 AGRUPAR: por nombre + reps + peso
            val grouped = remember(s.sets) {
                s.sets.groupBy { Triple(it.exerciseName, it.reps, it.weight) }
                    .map { (key, value) ->
                        GroupedRow(
                            exerciseName = key.first,
                            reps = key.second,
                            weight = key.third,
                            count = value.size
                        )
                    }
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(s.session.date)),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("${s.sets.size} sets", style = MaterialTheme.typography.bodyMedium)

                Divider()

                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(grouped) { row ->
                        ListItem(
                            headlineContent = { Text(row.exerciseName) },
                            supportingContent = {
                                Text("${row.reps} × ${row.weight} kg  •  Series: ${row.count}")
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

private data class GroupedRow(
    val exerciseName: String,
    val reps: Int,
    val weight: Double,
    val count: Int
)
