package com.example.superfitness.data.local.dao

import androidx.room.*
import com.example.superfitness.data.local.entity.AirQualityEntity
import com.example.superfitness.data.local.entity.ForecastWeatherEntity
import com.example.superfitness.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE id = 1")
    fun getWeather(): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM air_quality WHERE id = 1")
    fun getAirQuality(): Flow<AirQualityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirQuality(airQuality: AirQualityEntity)

    @Query("SELECT * FROM forecast_weather WHERE id = 1")
    fun getForecastWeather(): Flow<ForecastWeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastWeather(forecastWeather: ForecastWeatherEntity)

    @Query("DELETE FROM weather")
    suspend fun deleteWeather()
} 