package com.example.superfitness.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey val city: String,  // Tên thành phố
    val temperature: Float,          // Nhiệt độ
    val humidity: Float,             // Độ ẩm
    val airQualityIndex: Int,        // Chỉ số AQI
    val timestamp: Long              // Thời gian cập nhật (System.currentTimeMillis())
)
