package com.jorge.gymtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jorge.gymtracker.domain.model.Exercise

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val primaryMuscle: String,
    val secondaryMuscles: List<String>,
    val equipment: String?,      // ⬅️ opcionales como nullable
    val mechanics: String?,
    val difficulty: String?,
    val aliases: List<String>,
    val tags: List<String>
)

fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    primaryMuscle = primaryMuscle,
    secondaryMuscles = secondaryMuscles,
    equipment = equipment,
    mechanics = mechanics,
    difficulty = difficulty,
    aliases = aliases,
    tags = tags
)

fun Exercise.toEntity() = ExerciseEntity(
    id = id,
    name = name,
    primaryMuscle = primaryMuscle,
    secondaryMuscles = secondaryMuscles,
    equipment = equipment,
    mechanics = mechanics,
    difficulty = difficulty,
    aliases = aliases,
    tags = tags
)
