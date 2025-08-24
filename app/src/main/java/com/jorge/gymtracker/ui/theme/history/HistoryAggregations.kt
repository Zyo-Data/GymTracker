package com.jorge.gymtracker.ui.theme.history

/** Modelo común para representar sets agrupados */
data class AggregatedRow(
    val exerciseName: String,
    val weight: Double,
    val series: Int,
    val totalReps: Int
)

/** Helper para formatear pesos eliminando “.0” */
fun Double.removeTrailingZeros(): String {
    val s = toString()
    return if (s.endsWith(".0")) s.dropLast(2) else s
}
