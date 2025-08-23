package com.jorge.gymtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_set")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Int,
    val exerciseName: String,
    val reps: Int,
    val weight: Double
)
