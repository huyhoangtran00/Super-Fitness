package com.example.superfitness.domain.repository

import com.example.superfitness.data.remote.models.ApiWeather

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, long: Double): ApiWeather
}