package com.jorge.gymtracker.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jorge.gymtracker.domain.model.PRMetric
import com.jorge.gymtracker.domain.model.ProgressionScheme

/**
 * Un único objeto Converters con:
 * - Conversión List<String> ↔ JSON (Gson)
 * - Conversión de enums PRMetric / ProgressionScheme ↔ String
 *
 * NO pongas @TypeConverters aquí; esa anotación va en AppDb.
 */
object Converters {

    // ---- List<String> <-> JSON ----
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

    // ---- Enums PRMetric / ProgressionScheme ----
    @TypeConverter
    @JvmStatic
    fun fromPRMetric(v: PRMetric?): String? = v?.name

    @TypeConverter
    @JvmStatic
    fun toPRMetric(s: String?): PRMetric? =
        s?.let { PRMetric.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromProgressionScheme(v: ProgressionScheme?): String? = v?.name

    @TypeConverter
    @JvmStatic
    fun toProgressionScheme(s: String?): ProgressionScheme? =
        s?.let { ProgressionScheme.valueOf(it) }
}
