package com.example.superfitness.domain.models

data class Weather(
    val currentWeather: CurrentWeather,
    val daily: Daily,
    val hourly: Hourly
)