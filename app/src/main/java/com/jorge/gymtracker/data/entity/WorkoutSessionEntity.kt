package com.jorge.gymtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_session")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val title: String // ej: "Bíceps/Espalda — 22/08 15:40"
)
