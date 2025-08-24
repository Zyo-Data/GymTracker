package com.jorge.gymtracker.ui.theme.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jorge.gymtracker.data.entity.SessionWithSets
import com.jorge.gymtracker.data.repository.WorkoutRepository
import com.jorge.gymtracker.ui.theme.components.GymTopBar

/**
 * Detalle del historial: agrupa sets idénticos (mismo ejercicio + mismo peso)
 * y muestra "series: N" + reps totales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    sessionId: Long,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember { WorkoutRepository(ctx) }

    var detail by remember { mutableStateOf<SessionWithSets?>(null) }

    LaunchedEffect(sessionId) {
        // getSessionWithSets() es suspend: ok dentro de LaunchedEffect
        detail = repo.getSessionWithSets(sessionId)
    }

    // Agrupa sets por (ejercicio, peso) -> filas comprimidas
    val rows: List<AggregatedRow> = remember(detail) {
        (detail?.sets ?: emptyList())
            .groupBy { it.exerciseName to it.weight }
            .map { (key, group) ->
                val first = group.first()
                AggregatedRow(
                    exerciseName = first.exerciseName,
                    weight = key.second,
                    series = group.size,
                    totalReps = group.sumOf { it.reps }
                )
            }
            .sortedWith(
                compareBy<AggregatedRow> { it.exerciseName.lowercase() }
                    .thenByDescending { it.weight }
            )
    }

    Scaffold(
        topBar = {
            GymTopBar(title = "Detalle de sesión", canNavigateBack = true, onBack = onBack)
        }
    ) { inner ->
        if (detail == null) {
            Text("Cargando...", Modifier.padding(inner))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(rows) { row ->
                    ListItem(
                        headlineContent = { Text(row.exerciseName) },
                        supportingContent = {
                            Text(
                                "${row.weight.removeTrailingZeros()} kg • series: ${row.series} • ${row.totalReps} reps totales"
                            )
                        }
                    )
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                }
            }
        }
    }
}
