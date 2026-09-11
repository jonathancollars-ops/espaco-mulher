package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Patient::class,
        Assessment::class,
        Workout::class,
        WorkoutExercise::class,
        CustomExercise::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PilatesDatabase : RoomDatabase() {
    abstract fun pilatesDao(): PilatesDao

    companion object {
        @Volatile
        private var INSTANCE: PilatesDatabase? = null

        fun getDatabase(context: Context): PilatesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PilatesDatabase::class.java,
                    "pilates_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
