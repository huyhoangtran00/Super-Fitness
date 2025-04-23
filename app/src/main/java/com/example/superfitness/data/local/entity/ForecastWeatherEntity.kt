package com.example.superfitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.superfitness.data.local.converter.ForecastWeatherDataAdapter
import com.example.superfitness.domain.weather.ForecastWeatherData
import com.example.superfitness.domain.weather.ForecastWeatherInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

@Entity(tableName = "forecast_weather")
data class ForecastWeatherEntity(
    @PrimaryKey
    val id: Int = 1,
    val weatherDataPerDayJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double
) {
    fun toForecastWeatherInfo(): ForecastWeatherInfo {
        val type = object : TypeToken<List<Pair<String, ForecastWeatherData>>>() {}.type
        val gson = GsonBuilder()
            .registerTypeAdapter(type, ForecastWeatherDataAdapter())
            .create()
        val weatherDataPerDay = gson.fromJson<List<Pair<String, ForecastWeatherData>>>(weatherDataPerDayJson, type)
        return ForecastWeatherInfo(
            weatherDataPerDay = weatherDataPerDay
        )
    }

    companion object {
        fun fromForecastWeatherInfo(forecastWeatherInfo: ForecastWeatherInfo, lat: Double, long: Double): ForecastWeatherEntity {
            val type = object : TypeToken<List<Pair<String, ForecastWeatherData>>>() {}.type
            val gson = GsonBuilder()
                .registerTypeAdapter(type, ForecastWeatherDataAdapter())
                .create()
            return ForecastWeatherEntity(
                weatherDataPerDayJson = gson.toJson(forecastWeatherInfo.weatherDataPerDay),
                latitude = lat,
                longitude = long
            )
        }
    }
} 