package com.example.superfitness.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.superfitness.data.local.dao.RunDao
import com.example.superfitness.data.local.entity.RunEntity

@Database(
    entities = [
        RunEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "fitness-db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}