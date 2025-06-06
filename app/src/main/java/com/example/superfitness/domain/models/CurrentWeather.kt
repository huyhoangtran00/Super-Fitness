package com.example.superfitness.domain.models

import com.example.superfitness.utils.WeatherInfoItem

data class CurrentWeather(
    val temperature: Double,
    val time: String,
    val weatherStatus: WeatherInfoItem,
    val windDirection: String,
    val windSpeed: Double,
    val isDay: Boolean,
    val humidity: Int,
    val apparentTemperature: Double
)