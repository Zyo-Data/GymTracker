package com.jorge.gymtracker.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jorge.gymtracker.R
import com.jorge.gymtracker.data.entity.ExerciseEntity

object JsonUtils {
    fun loadExercisesFromRaw(context: Context, rawId: Int = R.raw.ejercicios_gimnasio_es): List<ExerciseEntity> {
        val json = context.resources.openRawResource(rawId).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<ExerciseEntity>>() {}.type
        return Gson().fromJson(json, type)
    }
}

