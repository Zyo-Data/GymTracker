package com.jorge.gymtracker.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithSets(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val sets: List<WorkoutSetEntity>
)
