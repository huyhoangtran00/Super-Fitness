package com.example.superfitness.data.remote.api

import com.squareup.moshi.Json

data class WeatherDataDto(
    val time: List<String>,
    @field:Json(name = "temperature_2m")
    val temperatures: List<Double>,
    @field:Json(name = "weathercode")
    val weatherCodes: List<Int>,
    @field:Json(name = "pressure_msl")
    val pressures: List<Double>,
    @field:Json(name = "windspeed_10m")
    val windSpeeds: List<Double>,
    @field:Json(name = "relativehumidity_2m")
    val humidities: List<Double>,
    @field:Json(name = "visibility")
    val visibility: List<Long>,
    @field:Json(name = "precipitation_probability")
    val precipitationList: List<Int>,
)

data class ForecastWeatherDataDto(
    val time: List<String>,
    @field:Json(name = "temperature_2m_max")
    val maxTemperatures: List<Double>,
    @field:Json(name = "temperature_2m_min")
    val minTemperatures: List<Double>,
    @field:Json(name = "weather_code")
    val weatherCodes: List<Int>,
    @field:Json(name = "precipitation_probability_max")
    val rainList: List<Int>,
    @field:Json(name = "visibility")
    val visibility: List<Long>,
    @field:Json(name = "uv_index_max")
    val uvIndexList: List<Float>,
)