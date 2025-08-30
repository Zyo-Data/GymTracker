package com.jorge.gymtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jorge.gymtracker.domain.model.PRMetric

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Int,               // usa el mismo tipo que ExerciseEntity.id
    val metric: PRMetric,
    val value: Double,                 // valor del PR (peso o 1RM estimado)
    val weight: Double? = null,        // peso asociado a MAX_REPS_AT_WEIGHT
    val reps: Int? = null,             // repeticiones asociadas (opcional)
    val date: Long = System.currentTimeMillis()
)
