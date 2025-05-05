package com.example.superfitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.superfitness.data.local.converter.WeatherDataConverter
import com.example.superfitness.data.mapper.IndexedWeatherData
import com.example.superfitness.domain.weather.WeatherData
import com.example.superfitness.domain.weather.WeatherInfo
import com.example.superfitness.domain.weather.WeatherType
import java.time.LocalDateTime

@Entity(tableName = "weather")
@TypeConverters(WeatherDataConverter::class)
data class WeatherEntity(
    @PrimaryKey
    val id: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val currentWeatherData: WeatherData?,
    val weatherDataPerDay: Map<Int, List<WeatherData>>,
    val allWeatherDataList: List<WeatherData>
) {
    fun toWeatherInfo(): WeatherInfo {
        return WeatherInfo(
            weatherDataPerDay = weatherDataPerDay,
            allWeatherDataList = allWeatherDataList.mapIndexed { index, data -> 
                IndexedWeatherData(index, data)
            },
            currentWeatherData = currentWeatherData,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromWeatherInfo(weatherInfo: WeatherInfo): WeatherEntity {
            return WeatherEntity(
                currentWeatherData = weatherInfo.currentWeatherData,
                weatherDataPerDay = weatherInfo.weatherDataPerDay,
                allWeatherDataList = weatherInfo.allWeatherDataList.map { it.data.copy(code = it.data.weatherType.code) }
            )
        }
    }
} 