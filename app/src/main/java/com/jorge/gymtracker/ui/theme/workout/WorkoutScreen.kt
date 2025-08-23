package com.jorge.gymtracker.ui.theme.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jorge.gymtracker.data.repository.ExerciseRepository
import com.jorge.gymtracker.data.repository.WorkoutRepository
import com.jorge.gymtracker.domain.model.Exercise
import kotlinx.coroutines.launch
import java.text.Normalizer

/* ---------- Helpers ---------- */

private fun String.normalize(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()

/** True si TODOS los tokens de la query están en alguno de los campos (name o primaryMuscle). */
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
    val count: Int = 1,        // 👈 contador de series iguales
)

/* ---------- Pantalla principal ---------- */

@Composable
fun WorkoutScreen() {
    val ctx = LocalContext.current
    val exerciseRepo = remember { ExerciseRepository(ctx) }
    val workoutRepo = remember { WorkoutRepository(ctx) }
    val scope = rememberCoroutineScope()

    // Estado ejercicios
    var all by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    LaunchedEffect(Unit) {
        all = exerciseRepo.getAll()
        println("DEBUG ▶ ejercicios cargados: ${all.size}")
    }

    // Estado buscador / selección
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember { mutableStateOf<Exercise?>(null) }

    // Estado inputs set
    var reps by remember { mutableStateOf(TextFieldValue("")) }
    var weight by remember { mutableStateOf(TextFieldValue("")) }
    var sets by remember { mutableStateOf(listOf<SetEntry>()) }

    // Filtrado multi-token
    val filtered by remember(query.text, all) {
        derivedStateOf {
            val q = query.text
            if (q.isBlank()) all
            else all.filter { ex -> matchesAllTokens(q, listOf(ex.name, ex.primaryMuscle)) }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Entrenamiento", style = MaterialTheme.typography.headlineSmall)

        ExerciseSearchPanel(
            query = query,
            suggestions = filtered,
            onQueryChange = { query = it },
            onPick = { ex ->
                selected = ex
                query = TextFieldValue(ex.name) // muestra lo elegido
            }
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
                        // Si ya existe una fila con mismo ejercicio+reps+peso, incrementa su contador.
                        val idx = sets.indexOfFirst { it.exerciseId == ex.id && it.reps == r && it.weight == w }
                        sets = if (idx >= 0) {
                            val current = sets[idx]
                            sets.toMutableList().apply {
                                set(idx, current.copy(count = (current.count + 1).coerceAtMost(50)))
                            }
                        } else {
                            sets + SetEntry(ex.id, ex.name, r, w, count = 1)
                        }

                        // ✅ Limpiar buscador y selección tras añadir
                        selected = null
                        query = TextFieldValue("")
                        reps = TextFieldValue("")
                        weight = TextFieldValue("")
                    }
                },
                enabled = selected != null,
                modifier = Modifier.height(56.dp)
            ) { Text("Añadir") }
        }

        Divider()

        // Lista de sets (cada fila con stepper de contador)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(sets, key = { _, s -> "${s.exerciseId}-${s.reps}-${s.weight}" }) { index, s ->
                ListItem(
                    headlineContent = {
                        Text("${s.exerciseName}: ${s.reps} × ${s.weight} kg")
                    },
                    supportingContent = {
                        Text("Volumen: ${(s.reps * s.weight * s.count).toInt()}  •  Series: ${s.count}")
                    },
                    trailingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val newCount = (s.count - 1).coerceAtLeast(1)
                                    sets = sets.toMutableList().apply {
                                        set(index, s.copy(count = newCount))
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text("–") }
                            Text("x${s.count}")
                            OutlinedButton(
                                onClick = {
                                    val newCount = (s.count + 1).coerceAtMost(50)
                                    sets = sets.toMutableList().apply {
                                        set(index, s.copy(count = newCount))
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text("+") }
                        }
                    }
                )
                // Botón para eliminar fila (opcional)
                Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    TextButton(onClick = {
                        sets = sets.toMutableList().also { it.removeAt(index) }
                    }) { Text("Eliminar") }
                }
                Divider()
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { sets = emptyList() }) { Text("Reset") }
            Button(
                onClick = {
                    scope.launch {
                        workoutRepo.saveSession(sets) // 👈 guarda expandiendo por count
                        println("💾 Sesión guardada con ${sets.sumOf { it.count }} sets totales")
                        sets = emptyList()
                    }
                },
                enabled = sets.isNotEmpty()
            ) { Text("Guardar sesión") }
        }
    }
}

/* ---------- Buscador con panel de sugerencias (altura fija) ---------- */

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
            value = query,
            onValueChange = onQueryChange,
            label = { Text(label) },
            singleLine = true,
            trailingIcon = {
                if (query.text.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange(TextFieldValue("")) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Limpiar")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { hasFocus = it.isFocused }
        )

        if (expanded) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(suggestions) { _, ex ->
                        ListItem(
                            headlineContent = { Text(ex.name) },
                            supportingContent = {
                                Text("${ex.primaryMuscle} • ${ex.equipment ?: "-"}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPick(ex)
                                    hasFocus = false
                                    focusManager.clearFocus()
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
