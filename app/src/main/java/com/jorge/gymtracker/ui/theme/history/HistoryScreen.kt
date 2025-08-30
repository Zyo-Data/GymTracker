package com.jorge.gymtracker.ui.theme.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jorge.gymtracker.data.db.AppDb
import com.jorge.gymtracker.data.entity.SessionWithSets
import com.jorge.gymtracker.data.repository.WorkoutRepository
import com.jorge.gymtracker.domain.PRService      // ✅ import correcto
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onOpenSession: (Long) -> Unit = {}
) {
    val ctx = LocalContext.current
    val db = remember { AppDb.get(ctx) }
    val prService = remember { PRService(db.workoutDao(), db.personalRecordDao(), db.progressionRuleDao()) }
    val workoutRepo = remember { WorkoutRepository(ctx, prService) }

    var sessions by remember { mutableStateOf<List<SessionWithSets>>(emptyList()) }

    LaunchedEffect(Unit) {
        sessions = workoutRepo.getHistoryWithSets()
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sessions) { s ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSession(s.session.id) }
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(s.session.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                .format(Date(s.session.date)),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("${s.sets.size} sets", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
