package com.jorge.gymtracker.data.seed

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.jorge.gymtracker.data.entity.ExerciseEntity
import java.io.BufferedReader
import java.io.InputStreamReader

object ExerciseSeeder {
    private const val TAG = "ExerciseSeeder"
    private val gson = Gson()

    /** Lee texto desde assets o raw. Cambia/añade nombres aquí si usas otros. */
    private fun readJson(context: Context): String? {
        // 1) assets/ejercicios.json
        runCatching {
            context.assets.open("ejercicios.json").use { it.bufferedReader().readText() }
        }.onSuccess { return it }

        // 2) res/raw/ejercicios_gimnasio_es.json
        val rawNames = listOf("ejercicios_gimnasio_es", "ejercicios", "exercises")
        for (name in rawNames) {
            val id = context.resources.getIdentifier(name, "raw", context.packageName)
            if (id != 0) {
                return runCatching {
                    context.resources.openRawResource(id).use { ins ->
                        BufferedReader(InputStreamReader(ins)).readText()
                    }
                }.onFailure { e ->
                    Log.e(TAG, "Error leyendo raw/$name: ${e.message}")
                }.getOrNull()
            }
        }
        Log.w(TAG, "No se encontró JSON en assets ni en raw.")
        return null
    }

    // DTO flexible (acepta español/inglés)
    private data class ExerciseSeedDto(
        val id: Int? = null,
        val name: String? = null,
        val nombre: String? = null,
        val primaryMuscle: String? = null,
        @SerializedName("musculoPrimario") val musculoPrimario: String? = null,
        val secondaryMuscles: List<String>? = null,
        @SerializedName("musculosSecundarios") val musculosSecundarios: List<String>? = null,
        val equipment: String? = null,
        val mechanics: String? = null,
        val difficulty: String? = null,
        val aliases: List<String>? = null,
        @SerializedName("alias") val alias: List<String>? = null,
        val tags: List<String>? = null
    ) {
        fun toEntity(): ExerciseEntity? {
            val finalId = id ?: return null
            val finalName = (name ?: nombre) ?: return null
            val finalPrimary = (primaryMuscle ?: musculoPrimario) ?: ""
            return ExerciseEntity(
                id = finalId,
                name = finalName,
                primaryMuscle = finalPrimary,
                secondaryMuscles = (secondaryMuscles ?: musculosSecundarios) ?: emptyList(),
                equipment = equipment,
                mechanics = mechanics,
                difficulty = difficulty,
                aliases = (aliases ?: alias) ?: emptyList(),
                tags = tags ?: emptyList()
            )
        }
    }

    /** Carga lista de ExerciseEntity desde assets o raw. */
    fun loadFromResources(context: Context): List<ExerciseEntity> {
        val json = readJson(context) ?: return emptyList()

        // 1) Intento directo a Entity
        val entityListType = object : TypeToken<List<ExerciseEntity>>() {}.type
        val direct = runCatching { gson.fromJson<List<ExerciseEntity>>(json, entityListType) }
            .getOrNull()
            ?.filterNotNull()
            ?: emptyList()
        if (direct.isNotEmpty()) {
            Log.i(TAG, "Seed directo: ${direct.size} ejercicios")
            return direct
        }

        // 2) Intento vía DTO flexible
        val dtoListType = object : TypeToken<List<ExerciseSeedDto>>() {}.type
        val dtos = runCatching { gson.fromJson<List<ExerciseSeedDto>>(json, dtoListType) }
            .getOrNull()
            ?: emptyList()
        val mapped = dtos.mapNotNull { it.toEntity() }
        Log.i(TAG, "Seed vía DTO: ${mapped.size} ejercicios")
        return mapped
    }
}
