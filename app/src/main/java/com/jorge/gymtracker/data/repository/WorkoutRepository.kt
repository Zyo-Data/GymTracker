package com.jorge.gymtracker.data.repository

import android.content.Context
import com.jorge.gymtracker.data.db.AppDb
import com.jorge.gymtracker.data.entity.SessionWithSets
import com.jorge.gymtracker.data.entity.WorkoutSessionEntity
import com.jorge.gymtracker.data.entity.WorkoutSetEntity
import com.jorge.gymtracker.ui.theme.workout.SetEntry
import java.text.SimpleDateFormat
import java.util.*

class WorkoutRepository(context: Context) {
    private val db = AppDb.get(context)
    private val workoutDao = db.workoutDao()
    private val exerciseDao = db.exerciseDao()

    // ✅ Método que usas en WorkoutScreen: guarda sets y calcula título automáticamente
    suspend fun saveSession(sets: List<SetEntry>) {
        // músculos primarios distintos de los ejercicios usados
        val ids = sets.map { it.exerciseId }.distinct()
        val exercises = if (ids.isNotEmpty()) exerciseDao.getByIds(ids) else emptyList()
        val muscles = exercises.map { it.primaryMuscle }.distinct()
        val musclesStr = if (muscles.isEmpty()) "Sesión" else muscles.joinToString("/")

        val title = "$musclesStr — ${
            java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        }"

        val sessionId = workoutDao.insertSession(
            com.jorge.gymtracker.data.entity.WorkoutSessionEntity(
                title = title,
                date = System.currentTimeMillis()
            )
        )

        // 👇 Expande cada fila según su count (xN series iguales)
        val setEntities = sets.flatMap { s ->
            List(s.count) {
                com.jorge.gymtracker.data.entity.WorkoutSetEntity(
                    sessionId = sessionId,
                    exerciseId = s.exerciseId,
                    exerciseName = s.exerciseName,
                    reps = s.reps,
                    weight = s.weight
                )
            }
        }
        workoutDao.insertSets(setEntities)
    }


    // Altas de bajo nivel (por si las necesitas)
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

}
