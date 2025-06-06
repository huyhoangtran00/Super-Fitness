package com.example.superfitness.data.remote.models


import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ApiHourlyWeather(
    @SerialName("rain")
    val rain: List<Double>,
    @SerialName("relative_humidity_2m")
    val relativeHumidity2m: List<Int>,
    @SerialName("temperature_2m")
    val temperature2m: List<Double>,
    @SerialName("time")
    val time: List<Long>,
    @SerialName("uv_index")
    val uvIndex: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("precipitation_probability")
    val rainProbability: List<Int>
)