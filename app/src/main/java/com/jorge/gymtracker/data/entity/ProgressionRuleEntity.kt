package com.jorge.gymtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jorge.gymtracker.domain.model.ProgressionScheme

@Entity(tableName = "progression_rules")
data class ProgressionRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Int? = null,       // null → regla por defecto
    val scheme: ProgressionScheme,
    val minReps: Int,
    val maxReps: Int,
    val sets: Int,
    val incrementKg: Double,
    val deloadPct: Double? = null      // ej. 0.1 (10% de descarga)
)
