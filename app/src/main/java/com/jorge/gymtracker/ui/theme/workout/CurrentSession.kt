package com.jorge.gymtracker.ui.theme.workout

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

data class SetEntry(
    val exerciseId: Int,
    val exerciseName: String,
    val reps: Int,
    val weight: Double,
    val count: Int = 1,
)

object CurrentSession {
    val sets = mutableStateListOf<SetEntry>()          // último arriba (index 0)
    var startedAt = mutableStateOf<Long?>(null)
        private set

    val totalSeries: Int get() = sets.sumOf { it.count }
    val totalEjercicios: Int get() = sets.map { it.exerciseId }.distinct().size

    fun startIfNeeded() {
        if (startedAt.value == null) startedAt.value = System.currentTimeMillis()
    }

    fun clear() {
        sets.clear()
        startedAt.value = null
    }

    /** Añade o incrementa series de un set idéntico; el último siempre queda arriba */
    fun addOrIncrement(newSet: SetEntry) {
        val idx = sets.indexOfFirst {
            it.exerciseId == newSet.exerciseId &&
                    it.reps == newSet.reps &&
                    it.weight == newSet.weight
        }
        if (idx >= 0) {
            val s = sets[idx].copy(count = (sets[idx].count + 1).coerceAtMost(50))
            sets.removeAt(idx)
            sets.add(0, s)
        } else {
            sets.add(0, newSet)
        }
    }

    fun updateAt(index: Int, updated: SetEntry) {
        if (index in sets.indices) sets[index] = updated
    }

    fun removeAt(index: Int) {
        if (index in sets.indices) sets.removeAt(index)
    }

    /** Segundos transcurridos desde startedAt (0 si no ha empezado). */
    fun elapsedSeconds(now: Long = System.currentTimeMillis()): Long {
        val start = startedAt.value ?: return 0L
        return ((now - start) / 1000L).coerceAtLeast(0L)
    }
}
