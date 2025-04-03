package com.example.superfitness.domain.weather

import com.example.superfitness.data.mapper.IndexedWeatherData

data class WeatherInfo(
    val weatherDataPerDay: Map<Int, List<WeatherData>>,
    val allWeatherDataList : List<IndexedWeatherData>,
    val currentWeatherData: WeatherData?
)

data class ForecastWeatherInfo(
    val weatherDataPerDay: List<Pair<String,ForecastWeatherData>>,
)