package com.jorge.gymtracker.data.dao

import androidx.room.*
import com.jorge.gymtracker.data.entity.PersonalRecordEntity
import com.jorge.gymtracker.domain.model.PRMetric

@Dao
interface PersonalRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pr: PersonalRecordEntity): Long

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId AND metric = :metric ORDER BY value DESC LIMIT 1")
    suspend fun getTopByMetric(exerciseId: Int, metric: PRMetric): PersonalRecordEntity?

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId AND metric = 'MAX_REPS_AT_WEIGHT' AND weight = :weight ORDER BY value DESC LIMIT 1")
    suspend fun getTopRepsAtWeight(exerciseId: Int, weight: Double): PersonalRecordEntity?
}
