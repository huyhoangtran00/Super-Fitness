package com.example.superfitness.domain.models

data class Weather(
    val currentWeather: CurrentWeather,
    val dailyWeather: DailyWeather,
    val hourlyWeather: HourlyWeather
)