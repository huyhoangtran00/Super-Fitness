package com.example.superfitness.data.remote.api

import com.squareup.moshi.Json

data class WeatherDto(
    @field:Json(name = "hourly")
    val weatherData: WeatherDataDto
)

data class ForecastWeatherDto(
    @field:Json(name = "daily")
    val weatherData: ForecastWeatherDataDto
)