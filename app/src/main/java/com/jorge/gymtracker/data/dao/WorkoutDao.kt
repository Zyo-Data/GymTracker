package com.jorge.gymtracker.data.dao

import androidx.room.*
import com.jorge.gymtracker.data.entity.SessionWithSets
import com.jorge.gymtracker.data.entity.WorkoutSessionEntity
import com.jorge.gymtracker.data.entity.WorkoutSetEntity

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)

    @Transaction
    @Query("SELECT * FROM workout_session ORDER BY date DESC")
    suspend fun getHistoryWithSets(): List<SessionWithSets>

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id LIMIT 1")
    suspend fun getSessionWithSets(id: Long): SessionWithSets?

    @Query("DELETE FROM workout_set WHERE sessionId = :sessionId")
    suspend fun deleteSetsBySessionId(sessionId: Long)

    @Query("DELETE FROM workout_session WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Transaction
    suspend fun deleteSessionWithSets(sessionId: Long) {
        deleteSetsBySessionId(sessionId)
        deleteSession(sessionId)
    }
    @Query("SELECT * FROM workout_set WHERE exerciseId = :exerciseId ORDER BY id DESC LIMIT :limit")
    suspend fun getLastSetsForExercise(exerciseId: Int, limit: Int): List<WorkoutSetEntity>


}
