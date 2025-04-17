package com.example.superfitness.data.local.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.local.db.dao.StepRecordDao
import com.example.superfitness.data.local.db.dao.WaterIntakeDao
import com.example.superfitness.data.local.db.dao.WeatherCacheDao
import com.example.superfitness.data.local.db.entity.Reminder
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.data.local.db.entity.StepRecord
import com.example.superfitness.data.local.db.entity.WaterIntake
import com.example.superfitness.data.local.db.entity.WeatherCache

@Database(
    entities = [
        UserProfile::class,
        StepRecord::class,
        WaterIntake::class,
        WeatherCache::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun stepRecordDao(): StepRecordDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Thêm log vào đoạn mã này để theo dõi
        fun getDatabase(context: Context): AppDatabase {
            // Kiểm tra nếu INSTANCE là null và log thông báo
            Log.d("AppDatabase", "Attempting to get database instance.")

            return INSTANCE ?: synchronized(this) {
                Log.d("AppDatabase", "Creating new database instance.")
                val instance = try {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "super_fitness_database"
                    )
                        .fallbackToDestructiveMigration()  // ✅ thêm dòng này
                        .build()
                } catch (e: Exception) {
                    Log.e("AppDatabase", "Error creating database: ${e.message}")
                    throw e
                }
                INSTANCE = instance
                instance
            }
        }
    }
}
