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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jorge.gymtracker.data.repository.ExerciseRepository
import com.jorge.gymtracker.data.repository.WorkoutRepository
import com.jorge.gymtracker.domain.model.Exercise
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.Normalizer

// DB + PRService para el repositorio (si tu repo los necesita)
import com.jorge.gymtracker.data.db.AppDb
import com.jorge.gymtracker.domain.PRService

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

private fun fmtHMS(total: Long): String {
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

/* ---------- Temporizador descanso (igual que tu original) ---------- */
@Composable
private fun RestTimer(
    modifier: Modifier = Modifier,
    resetSignal: Int
) {
    val haptics = LocalHapticFeedback.current
    var isRunning by rememberSaveable(resetSignal) { mutableStateOf(false) }
    var hasStarted by rememberSaveable(resetSignal) { mutableStateOf(false) }
    var remaining by rememberSaveable(resetSignal) { mutableIntStateOf(0) }

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
            Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { remaining = (remaining - 10).coerceAtLeast(0) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text("-10") }
                Text(text = format(remaining), style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                OutlinedButton(onClick = { remaining += 10 },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text("+10") }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        if (!isRunning && remaining > 0) hasStarted = true
                        isRunning = !isRunning
                    },
                    enabled = remaining > 0
                ) {
                    Text( when {
                        isRunning -> "Pausar"
                        hasStarted && remaining > 0 -> "Reanudar"
                        else -> "Start"
                    })
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

/* ---------- Fila Set compacta (la mantenemos para el último añadido) ---------- */
@Composable
private fun SetRowCompact(
    s: SetEntry,
    onDec: () -> Unit,
    onInc: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(s.exerciseName, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                Text("${s.reps} × ${"%.2f".format(s.weight)} kg", style = MaterialTheme.typography.bodySmall)
            }
            FilledTonalButton(onClick = {}, enabled = false, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                Text("Series: ${s.count}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onDec, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)) { Text("–") }
                OutlinedButton(onClick = onInc, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)) { Text("+") }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Close, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/* ---------- Barra Sesión actual (clicable) ---------- */
@Composable
private fun SessionInfoBar(
    ejercicios: Int,
    series: Int,
    elapsedSec: Long,
    onOpen: () -> Unit
) {
    ElevatedCard(onClick = onOpen) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Sesión actual", style = MaterialTheme.typography.titleSmall)
                Text("Tiempo: ${fmtHMS(elapsedSec)}", style = MaterialTheme.typography.bodySmall)
            }
            Text("$ejercicios ejercicios • $series series", style = MaterialTheme.typography.bodySmall)
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
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/* ---------- Pantalla principal ---------- */
@Composable
fun WorkoutScreen(navController: NavController? = null) {
    val ctx = LocalContext.current
    // Si tu WorkoutRepository requiere PRService:
    val db = remember { AppDb.get(ctx) }
    val prService = remember { PRService(db.workoutDao(), db.personalRecordDao(), db.progressionRuleDao()) }
    val exerciseRepo = remember { ExerciseRepository(ctx) }
    val workoutRepo = remember { WorkoutRepository(ctx, prService) }

    val scope = rememberCoroutineScope()
    var all by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    LaunchedEffect(Unit) { all = exerciseRepo.getAll() }

    var query by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember { mutableStateOf<Exercise?>(null) }
    var reps by remember { mutableStateOf(TextFieldValue("")) }
    var weight by remember { mutableStateOf(TextFieldValue("")) }

    // ⏱ cronómetro sesión (ticker)
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(CurrentSession.startedAt.value) {
        if (CurrentSession.startedAt.value != null) {
            while (isActive && CurrentSession.startedAt.value != null) {
                delay(1000); tick++
            }
        }
    }
    val elapsedSec by remember(CurrentSession.startedAt.value, tick) {
        mutableStateOf(CurrentSession.elapsedSeconds())
    }

    // descanso timer reset
    var resetCounter by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val filtered by remember(query.text, all) {
        derivedStateOf {
            if (query.text.isBlank()) all
            else all.filter { ex -> matchesAllTokens(query.text, listOf(ex.name, ex.primaryMuscle)) }
        }
    }

    // Volumen total
    val totalVolume by remember { derivedStateOf { CurrentSession.sets.sumOf { (it.reps * it.weight * it.count).toInt() } } }

    Scaffold(
        snackbarHost = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(bottom = 56.dp))
            }
        }
    ) { innerPad ->
        Column(
            Modifier.fillMaxSize().padding(innerPad).padding(12.dp),
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
                            CurrentSession.startIfNeeded()
                            CurrentSession.addOrIncrement(SetEntry(ex.id, ex.name, r, w, 1))

                            // limpiar inputs
                            selected = null
                            query = TextFieldValue("")
                            reps = TextFieldValue("")
                            weight = TextFieldValue("")
                        }
                    },
                    enabled = selected != null,
                    modifier = Modifier.height(52.dp)
                ) { Text("Añadir") }
            }

            HorizontalDivider()

            // Temporizador descanso
            RestTimer(modifier = Modifier.fillMaxWidth(), resetSignal = resetCounter)

            // Sesión actual (abre la lista completa)
            SessionInfoBar(
                ejercicios = CurrentSession.totalEjercicios,
                series = CurrentSession.totalSeries,
                elapsedSec = elapsedSec,
                onOpen = { navController?.navigate("currentSession") }
            )

            // ▼▼▼ SOLO mostrar el último set añadido (si existe) ▼▼▼
            val last = CurrentSession.sets.firstOrNull()
            if (last != null) {
                SetRowCompact(
                    s = last,
                    onDec = {
                        val idx = 0
                        val newCount = (last.count - 1).coerceAtLeast(1)
                        CurrentSession.updateAt(idx, last.copy(count = newCount))
                    },
                    onInc = {
                        val idx = 0
                        val newCount = (last.count + 1).coerceAtMost(50)
                        CurrentSession.updateAt(idx, last.copy(count = newCount))
                    },
                    onDelete = {
                        CurrentSession.removeAt(0)
                    }
                )
            }

            // Fila final
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Volumen total: $totalVolume", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            CurrentSession.clear()
                            selected = null
                            query = TextFieldValue("")
                            reps = TextFieldValue("")
                            weight = TextFieldValue("")
                            resetCounter++   // reinicia temporizador descanso
                        },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) { Text("Reset") }

                    Button(
                        onClick = {
                            scope.launch {
                                workoutRepo.saveSession(CurrentSession.sets)
                                CurrentSession.clear()
                                selected = null
                                query = TextFieldValue("")
                                reps = TextFieldValue("")
                                weight = TextFieldValue("")
                                resetCounter++   // reinicia temporizador descanso
                                snackbarHostState.showSnackbar("Sesión guardada", duration = SnackbarDuration.Short)
                            }
                        },
                        enabled = CurrentSession.sets.isNotEmpty(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) { Text("Guardar sesión") }
                }
            }
        }
    }
}
