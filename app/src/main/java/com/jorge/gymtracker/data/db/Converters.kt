package com.jorge.gymtracker.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    @JvmStatic
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return gson.fromJson(json, listType)
    }
}
