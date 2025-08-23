package com.jorge.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jorge.gymtracker.data.entity.ExerciseEntity

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ExerciseEntity>)

    @Query("SELECT COUNT(*) FROM exercise")
    suspend fun count(): Int

    @Query("SELECT * FROM exercise ORDER BY name")
    suspend fun getAll(): List<ExerciseEntity>

    @Query("""
        SELECT * FROM exercise
        WHERE name LIKE :q OR primaryMuscle LIKE :q
        ORDER BY name
    """)
    suspend fun searchSimple(q: String): List<ExerciseEntity>

    // ⬇️ ESTA ES LA FUNCIÓN QUE TENÍAS EN WorkoutDao
    @Query("SELECT * FROM exercise WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<ExerciseEntity>
}
