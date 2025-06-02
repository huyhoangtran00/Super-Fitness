package com.example.superfitness.data.remote.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiHourlyWeather(
    @SerialName("cloud_cover")
    val cloudCover: List<Int>,
    @SerialName("rain")
    val rain: List<Double>,
    @SerialName("relative_humidity_2m")
    val relativeHumidity2m: List<Int>,
    @SerialName("temperature_2m")
    val temperature2m: List<Double>,
    @SerialName("time")
    val time: List<Int>,
    @SerialName("weather_code")
    val weatherCode: List<Int>
)