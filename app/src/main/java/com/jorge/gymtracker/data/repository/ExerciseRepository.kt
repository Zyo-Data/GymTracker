package com.jorge.gymtracker.data.repository

import android.content.Context
import android.util.Log
import com.jorge.gymtracker.data.db.AppDb
import com.jorge.gymtracker.data.entity.toDomain
import com.jorge.gymtracker.data.seed.ExerciseSeeder
import com.jorge.gymtracker.domain.model.Exercise

class ExerciseRepository(private val context: Context) {
    private val dao = AppDb.get(context).exerciseDao()

    private suspend fun ensureSeed() {
        val count = dao.count()
        Log.d("ExerciseRepository", "count antes de seed = $count")
        if (count == 0) {
            val seed = ExerciseSeeder.loadFromResources(context) // 👈 ahora lee assets o raw
            Log.d("ExerciseRepository", "seed cargado = ${seed.size}")
            if (seed.isNotEmpty()) {
                dao.insertAll(seed)
                Log.d("ExerciseRepository", "seed insertado")
            } else {
                Log.w("ExerciseRepository", "seed vacío: revisa el JSON en res/raw o assets")
            }
        }
    }

    suspend fun getAll(): List<Exercise> {
        ensureSeed()
        val out = dao.getAll().map { it.toDomain() }
        Log.d("ExerciseRepository", "getAll() -> ${out.size}")
        return out
    }

    suspend fun search(query: String): List<Exercise> {
        ensureSeed()
        if (query.isBlank()) return getAll()
        val out = dao.searchSimple("%$query%").map { it.toDomain() }
        Log.d("ExerciseRepository", "search('$query') -> ${out.size}")
        return out
    }
}
