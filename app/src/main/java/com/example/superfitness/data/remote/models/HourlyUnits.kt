package com.example.superfitness.data.remote.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HourlyUnits(
    @SerialName("cloud_cover")
    val cloudCover: String,
    @SerialName("rain")
    val rain: String,
    @SerialName("relative_humidity_2m")
    val relativeHumidity2m: String,
    @SerialName("temperature_2m")
    val temperature2m: String,
    @SerialName("time")
    val time: String,
    @SerialName("weather_code")
    val weatherCode: String
)