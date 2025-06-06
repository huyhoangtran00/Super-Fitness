package com.example.superfitness.data.remote.models

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ApiCurrentWeather(
    @SerialName("interval")
    val interval: Int,
    @SerialName("is_day")
    val isDay: Int,
    @SerialName("relative_humidity_2m")
    val relativeHumidity2m: Int,
    @SerialName("temperature_2m")
    val temperature2m: Double,
    @SerialName("time")
    val time: Long,
    @SerialName("weather_code")
    val weatherCode: Int,
    @SerialName("wind_direction_10m")
    val windDirection10m: Double,
    @SerialName("wind_speed_10m")
    val windSpeed10m: Double,
    @SerialName("apparent_temperature")
    val apparentTemperature: Double
)