package com.jorge.gymtracker.ui.theme.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jorge.gymtracker.data.repository.ExerciseRepository
import com.jorge.gymtracker.data.repository.WorkoutRepository
import com.jorge.gymtracker.domain.model.Exercise
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.Normalizer

/* ---------- Helpers ---------- */
private fun String.normalize(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()

private fun matchesAllTokens(query: String, fields: List<String?>): Boolean {
    if (query.isBlank()) return true
    val tokens = query.normalize().split(" ").filter { it.isNotBlank() }
    if (tokens.isEmpty()) return true
    val haystack = fields.filterNotNull().joinToString(" ").normalize()
    return tokens.all { haystack.contains(it) }
}

/* ---------- Modelo UI ---------- */
data class SetEntry(
    val exerciseId: Int,
    val exerciseName: String,
    val reps: Int,
    val weight: Double,
    val count: Int = 1, // series iguales
)

/* ---------- Temporizador (mockup centrado, tiempo más grande con -10/+10) ---------- */
@Composable
private fun RestTimer(
    modifier: Modifier = Modifier,
    resetSignal: Int
) {
    val haptics = LocalHapticFeedback.current
    var isRunning by rememberSaveable(resetSignal) { mutableStateOf(false) }
    var hasStarted by rememberSaveable(resetSignal) { mutableStateOf(false) }
    var remaining by rememberSaveable(resetSignal) { mutableStateOf(0) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isActive && remaining > 0) {
                delay(1000)
                remaining -= 1
            }
            if (remaining == 0) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            isRunning = false
        }
    }

    fun format(sec: Int): String = "%02d:%02d".format(sec / 60, sec % 60)

    Card(modifier = modifier, shape = MaterialTheme.shapes.medium) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fila 1 — Presets centrados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30, 60, 90, 120, 180).forEach { sec ->
                        AssistChip(
                            onClick = { isRunning = false; hasStarted = false; remaining = sec },
                            label = { Text("${sec}s") }
                        )
                    }
                }
            }

            // Fila 2 — Tiempo grande con -10 / +10 al lado
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { remaining = (remaining - 10).coerceAtLeast(0) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) { Text("-10") }

                Text(
                    text = format(remaining),
                    style = MaterialTheme.typography.displayMedium, // más grande
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedButton(
                    onClick = { remaining = remaining + 10 },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) { Text("+10") }
            }

            // Fila 3 — Start grande + Reset pequeño a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (!isRunning && remaining > 0) hasStarted = true
                        isRunning = !isRunning
                    },
                    enabled = remaining > 0
                ) {
                    Text(
                        when {
                            isRunning -> "Pausar"
                            hasStarted && remaining > 0 -> "Reanudar"
                            else -> "Start"
                        }
                    )
                }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(
                    onClick = { isRunning = false; hasStarted = false; remaining = 0 },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) { Text("Reset") }
            }
        }
    }
}


/* ---------- Tarjeta compacta de set (énfasis en Series + icono X) ---------- */
@Composable
private fun SetRowCompact(
    s: SetEntry,
    onDec: () -> Unit,
    onInc: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    s.exerciseName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    "${s.reps} × ${s.weight} kg",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Más visible
            FilledTonalButton(
                onClick = {},
                enabled = false,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Series: ${s.count}", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.width(8.dp))

            // Controles compactos: −  +  X
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDec,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) { Text("–") }
                OutlinedButton(
                    onClick = onInc,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) { Text("+") }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    // quitado el link "Eliminar" de debajo
}

/* ---------- Pantalla principal ---------- */
@Composable
fun WorkoutScreen() {
    val ctx = LocalContext.current
    val exerciseRepo = remember { ExerciseRepository(ctx) }
    val workoutRepo = remember { WorkoutRepository(ctx) }
    val scope = rememberCoroutineScope()

    var all by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    LaunchedEffect(Unit) { all = exerciseRepo.getAll() }

    var query by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember { mutableStateOf<Exercise?>(null) }
    var reps by remember { mutableStateOf(TextFieldValue("")) }
    var weight by remember { mutableStateOf(TextFieldValue("")) }
    var sets by remember { mutableStateOf(listOf<SetEntry>()) }
    val listState = rememberLazyListState()

    var resetCounter by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val filtered by remember(query.text, all) {
        derivedStateOf {
            if (query.text.isBlank()) all
            else all.filter { ex -> matchesAllTokens(query.text, listOf(ex.name, ex.primaryMuscle)) }
        }
    }

    // Volumen total (solo resumen)
    val totalVolume by remember(sets) {
        mutableStateOf(sets.sumOf { (it.reps * it.weight * it.count).toInt() })
    }

    Scaffold(
        snackbarHost = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 56.dp) // deja sitio para la BottomBar
                )
            }
        }
    ) { innerPad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPad)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Entrenamiento", style = MaterialTheme.typography.headlineSmall)

            ExerciseSearchPanel(
                query = query,
                suggestions = filtered,
                onQueryChange = { query = it },
                onPick = { ex -> selected = ex; query = TextFieldValue(ex.name) }
            )

            Text(selected?.let { "Seleccionado: ${it.name}" } ?: "Selecciona un ejercicio")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = reps, onValueChange = { reps = it },
                    label = { Text("Reps") }, singleLine = true, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = weight, onValueChange = { weight = it },
                    label = { Text("Peso (kg)") }, singleLine = true, modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val r = reps.text.toIntOrNull()
                        val w = weight.text.toDoubleOrNull()
                        val ex = selected
                        if (ex != null && r != null && r > 0 && w != null && w >= 0.0) {
                            val idx = sets.indexOfFirst { it.exerciseId == ex.id && it.reps == r && it.weight == w }
                            sets = if (idx >= 0) {
                                val updated = sets[idx].copy(count = (sets[idx].count + 1).coerceAtMost(50))
                                listOf(updated) + sets.filterIndexed { i, _ -> i != idx }
                            } else {
                                listOf(SetEntry(ex.id, ex.name, r, w, 1)) + sets
                            }
                            selected = null
                            query = TextFieldValue("")
                            reps = TextFieldValue("")
                            weight = TextFieldValue("")
                            scope.launch { listState.scrollToItem(0) }
                        }
                    },
                    enabled = selected != null, modifier = Modifier.height(52.dp)
                ) { Text("Añadir") }
            }

            Divider()

            // Temporizador (mockup centrado)
            RestTimer(modifier = Modifier.fillMaxWidth(), resetSignal = resetCounter)

            // Lista compacta (≥ 3 caben sin scroll), último añadido arriba
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(sets, key = { _, s -> "${s.exerciseId}-${s.reps}-${s.weight}" }) { index, s ->
                    SetRowCompact(
                        s = s,
                        onDec = {
                            val newCount = (s.count - 1).coerceAtLeast(1)
                            val updated = s.copy(count = newCount)
                            sets = listOf(updated) + sets.filterIndexed { i, _ -> i != index }
                        },
                        onInc = {
                            val newCount = (s.count + 1).coerceAtMost(50)
                            val updated = s.copy(count = newCount)
                            sets = listOf(updated) + sets.filterIndexed { i, _ -> i != index }
                        },
                        onDelete = { sets = sets.toMutableList().also { it.removeAt(index) } }
                    )
                }
            }
// Fila final: Volumen total + botones, pegados a la BottomBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp), // 👈 elimina aire extra
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Volumen total: $totalVolume",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            sets = emptyList()
                            selected = null
                            query = TextFieldValue("")
                            reps = TextFieldValue("")
                            weight = TextFieldValue("")
                            resetCounter++
                        },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) { Text("Reset") }

                    Button(
                        onClick = {
                            scope.launch {
                                workoutRepo.saveSession(sets)
                                // Reset total
                                sets = emptyList()
                                selected = null
                                query = TextFieldValue("")
                                reps = TextFieldValue("")
                                weight = TextFieldValue("")
                                resetCounter++
                                snackbarHostState.showSnackbar(
                                    message = "Sesión guardada",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        enabled = sets.isNotEmpty(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) { Text("Guardar sesión") }
                }
            }
        }
    }
}

/* ---------- Buscador ---------- */
@Composable
private fun ExerciseSearchPanel(
    query: TextFieldValue,
    suggestions: List<Exercise>,
    onQueryChange: (TextFieldValue) -> Unit,
    onPick: (Exercise) -> Unit,
    label: String = "Buscar (nombre o músculo)"
) {
    var hasFocus by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val expanded by remember(hasFocus, query.text, suggestions) {
        mutableStateOf(hasFocus && query.text.isNotBlank() && suggestions.isNotEmpty())
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = query, onValueChange = onQueryChange,
            label = { Text(label) }, singleLine = true,
            trailingIcon = {
                if (query.text.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange(TextFieldValue("")) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Limpiar")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().onFocusChanged { hasFocus = it.isFocused }
        )
        if (expanded) {
            ElevatedCard(Modifier.fillMaxWidth().height(300.dp)) {
                LazyColumn(Modifier.fillMaxWidth()) {
                    itemsIndexed(suggestions) { _, ex ->
                        ListItem(
                            headlineContent = { Text(ex.name) },
                            supportingContent = { Text("${ex.primaryMuscle} • ${ex.equipment ?: "-"}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPick(ex); hasFocus = false; focusManager.clearFocus()
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                        Divider()
                    }
                }
            }
        }
    }
}
