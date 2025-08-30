package com.jorge.gymtracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jorge.gymtracker.data.dao.ExerciseDao
import com.jorge.gymtracker.data.dao.WorkoutDao
import com.jorge.gymtracker.data.dao.PersonalRecordDao
import com.jorge.gymtracker.data.dao.ProgressionRuleDao
import com.jorge.gymtracker.data.entity.ExerciseEntity
import com.jorge.gymtracker.data.entity.WorkoutSessionEntity
import com.jorge.gymtracker.data.entity.WorkoutSetEntity
import com.jorge.gymtracker.data.entity.PersonalRecordEntity
import com.jorge.gymtracker.data.entity.ProgressionRuleEntity

/**
 * Base de datos Room principal.
 * Asegúrate de que PersonalRecordEntity y ProgressionRuleEntity existen
 * en el paquete com.jorge.gymtracker.data.entity.
 */
@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        PersonalRecordEntity::class,
        ProgressionRuleEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {

    // DAOs
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun progressionRuleDao(): ProgressionRuleDao

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
                    // Durante desarrollo: recrea DB si cambia el esquema
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
