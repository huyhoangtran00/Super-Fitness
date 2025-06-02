package com.example.superfitness.data.remote.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiDailyWeather(
    @SerialName("rain_sum")
    val rainSum: List<Double>,
    @SerialName("temperature_2m_max")
    val temperature2mMax: List<Double>,
    @SerialName("temperature_2m_min")
    val temperature2mMin: List<Double>,
    @SerialName("time")
    val time: List<Int>,
    @SerialName("uv_index_max")
    val uvIndexMax: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("wind_direction_10m_dominant")
    val windDirection10mDominant: List<Int>,
    @SerialName("wind_speed_10m_max")
    val windSpeed10mMax: List<Double>
)