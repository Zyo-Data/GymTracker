package com.jorge.gymtracker.domain.model

data class Exercise(
    val id: Int,
    val name: String,
    val primaryMuscle: String,
    val secondaryMuscles: List<String> = emptyList(),
    val equipment: String? = null,
    val mechanics: String? = null,
    val difficulty: String? = null,
    val aliases: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)
