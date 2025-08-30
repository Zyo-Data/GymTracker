package com.jorge.gymtracker.ui.theme.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentSessionScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sesión actual") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Resumen",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "${CurrentSession.totalEjercicios} ejercicios • ${CurrentSession.totalSeries} series",
                style = MaterialTheme.typography.bodyMedium
            )

            Divider()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(CurrentSession.sets) { index, s ->
                    ElevatedCard {
                        Column(Modifier.padding(10.dp)) {
                            Text(s.exerciseName, style = MaterialTheme.typography.titleSmall)
                            Text("${s.reps} × ${"%.2f".format(s.weight)} kg", style = MaterialTheme.typography.bodyMedium)
                            Text("Series: ${s.count}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
