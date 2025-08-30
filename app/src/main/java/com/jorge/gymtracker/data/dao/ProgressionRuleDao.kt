package com.jorge.gymtracker.data.dao

import androidx.room.*
import com.jorge.gymtracker.data.entity.ProgressionRuleEntity

@Dao
interface ProgressionRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rule: ProgressionRuleEntity): Long

    @Query("SELECT * FROM progression_rules WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getByExercise(exerciseId: Int): ProgressionRuleEntity?

    @Query("SELECT * FROM progression_rules WHERE exerciseId IS NULL LIMIT 1")
    suspend fun getDefault(): ProgressionRuleEntity?
}
