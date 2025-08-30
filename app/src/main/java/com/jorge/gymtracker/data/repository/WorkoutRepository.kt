package com.jorge.gymtracker.data.repository

import android.content.Context
import com.jorge.gymtracker.data.db.AppDb
import com.jorge.gymtracker.data.entity.SessionWithSets
import com.jorge.gymtracker.data.entity.WorkoutSessionEntity
import com.jorge.gymtracker.data.entity.WorkoutSetEntity
import com.jorge.gymtracker.ui.theme.workout.SetEntry
import com.jorge.gymtracker.domain.PRService   // ✅ import correcto (no usecase)
import java.text.SimpleDateFormat
import java.util.*

class WorkoutRepository(
    context: Context,
    private val prService: PRService             // ✅ parámetro correcto
) {
    private val db = AppDb.get(context)
    private val workoutDao = db.workoutDao()
    private val exerciseDao = db.exerciseDao()

    // Guarda sesión y actualiza PRs
    suspend fun saveSession(sets: List<SetEntry>) {
        val ids = sets.map { it.exerciseId }.distinct()
        val exercises = if (ids.isNotEmpty()) exerciseDao.getByIds(ids) else emptyList()
        val muscles = exercises.map { it.primaryMuscle }.distinct()
        val musclesStr = if (muscles.isEmpty()) "Sesión" else muscles.joinToString("/")

        val title = "$musclesStr — ${
            SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())
        }"

        val sessionId = workoutDao.insertSession(
            WorkoutSessionEntity(
                title = title,
                date = System.currentTimeMillis()
            )
        )

        val setEntities = sets.flatMap { s ->
            List(s.count) {
                WorkoutSetEntity(
                    sessionId = sessionId,
                    exerciseId = s.exerciseId,
                    exerciseName = s.exerciseName,
                    reps = s.reps,
                    weight = s.weight
                )
            }
        }
        workoutDao.insertSets(setEntities)

        // ✅ actualiza PRs
        prService.updatePRsForSession(setEntities)
    }

    // Altas de bajo nivel
    suspend fun insertSession(session: WorkoutSessionEntity): Long =
        workoutDao.insertSession(session)

    suspend fun insertSets(sets: List<WorkoutSetEntity>) =
        workoutDao.insertSets(sets)

    // Lecturas
    suspend fun getHistoryWithSets(): List<SessionWithSets> =
        workoutDao.getHistoryWithSets()

    suspend fun getSessionWithSets(id: Long): SessionWithSets? =
        workoutDao.getSessionWithSets(id)

    suspend fun deleteSession(id: Long) {
        workoutDao.deleteSessionWithSets(id)
    }

    // ✅ sugerencia próxima carga
    suspend fun suggestNextLoad(exerciseId: Int): Double? =
        prService.suggestNextLoad(exerciseId)
}
