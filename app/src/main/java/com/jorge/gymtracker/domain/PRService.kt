package com.jorge.gymtracker.domain

import com.jorge.gymtracker.data.dao.PersonalRecordDao
import com.jorge.gymtracker.data.dao.ProgressionRuleDao
import com.jorge.gymtracker.data.dao.WorkoutDao
import com.jorge.gymtracker.data.entity.PersonalRecordEntity
import com.jorge.gymtracker.data.entity.WorkoutSetEntity
import com.jorge.gymtracker.domain.model.PRMetric
import com.jorge.gymtracker.domain.model.ProgressionScheme
import kotlin.math.round

class PRService(
    private val workoutDao: WorkoutDao,
    private val prDao: PersonalRecordDao,
    private val ruleDao: ProgressionRuleDao
) {

    /**
     * Recorre los sets guardados de una sesión y actualiza tres tipos de PR:
     * - MAX_WEIGHT (peso máximo levantado)
     * - MAX_REPS_AT_WEIGHT (máximas reps para un peso concreto)
     * - E1RM (1RM estimado con fórmula Epley)
     */
    suspend fun updatePRsForSession(sets: List<WorkoutSetEntity>) {
        for (set in sets) {
            // 1) MAX_WEIGHT
            val currentMaxWeight = prDao.getTopByMetric(set.exerciseId, PRMetric.MAX_WEIGHT)
            if (currentMaxWeight == null || set.weight > currentMaxWeight.value) {
                prDao.insert(
                    PersonalRecordEntity(
                        exerciseId = set.exerciseId,
                        metric = PRMetric.MAX_WEIGHT,
                        value = set.weight,
                        date = System.currentTimeMillis()
                    )
                )
            }

            // 2) MAX_REPS_AT_WEIGHT
            val currentMaxRepsAtWeight = prDao.getTopRepsAtWeight(set.exerciseId, set.weight)
            if (currentMaxRepsAtWeight == null || set.reps > (currentMaxRepsAtWeight.value).toInt()) {
                prDao.insert(
                    PersonalRecordEntity(
                        exerciseId = set.exerciseId,
                        metric = PRMetric.MAX_REPS_AT_WEIGHT,
                        value = set.reps.toDouble(),
                        weight = set.weight,
                        reps = set.reps,
                        date = System.currentTimeMillis()
                    )
                )
            }

            // 3) E1RM (Epley)
            val est1RM = set.weight * (1.0 + set.reps / 30.0)
            val currentE1Rm = prDao.getTopByMetric(set.exerciseId, PRMetric.E1RM)
            if (currentE1Rm == null || est1RM > currentE1Rm.value) {
                prDao.insert(
                    PersonalRecordEntity(
                        exerciseId = set.exerciseId,
                        metric = PRMetric.E1RM,
                        value = est1RM,
                        date = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    /**
     * Devuelve la carga sugerida para el próximo entrenamiento del ejercicio.
     * Usa:
     *  - Regla específica del ejercicio, o la regla por defecto si no hay.
     *  - Últimos N sets (N = rule.sets) para calcular reps medias.
     *  - DOUBLE_PROGRESSION: si medias >= maxReps, sube peso; si no, mantén.
     *  - LINEAR_LOAD: siempre sube peso con el incremento.
     */
    suspend fun suggestNextLoad(exerciseId: Int): Double? {
        val rule = ruleDao.getByExercise(exerciseId) ?: ruleDao.getDefault() ?: return null
        val lastSets = workoutDao.getLastSetsForExercise(exerciseId, rule.sets)
        if (lastSets.isEmpty()) return null

        val avgReps = lastSets.map { it.reps }.average()
        val lastWeight = lastSets.first().weight

        val next = when (rule.scheme) {
            ProgressionScheme.DOUBLE_PROGRESSION ->
                if (avgReps >= rule.maxReps) lastWeight + rule.incrementKg else lastWeight

            ProgressionScheme.LINEAR_LOAD ->
                lastWeight + rule.incrementKg
        }

        return round2(next)
    }

    private fun round2(x: Double): Double = round(x * 100.0) / 100.0
}
