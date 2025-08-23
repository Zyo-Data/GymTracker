package com.jorge.gymtracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jorge.gymtracker.data.dao.ExerciseDao
import com.jorge.gymtracker.data.dao.WorkoutDao
import com.jorge.gymtracker.data.entity.ExerciseEntity
import com.jorge.gymtracker.data.entity.WorkoutSessionEntity
import com.jorge.gymtracker.data.entity.WorkoutSetEntity

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)   // ⬅️ añadimos los conversores
abstract class AppDb : RoomDatabase() {

    // DAOs
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun get(context: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "app.db"
                )
                    // ⚠️ Destruye y recrea la DB si cambia el esquema (útil durante desarrollo)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
