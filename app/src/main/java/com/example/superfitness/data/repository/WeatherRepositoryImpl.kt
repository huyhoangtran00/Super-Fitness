package com.example.superfitness.data.repository

import com.example.superfitness.data.remote.WeatherApi
import com.example.superfitness.data.remote.models.ApiWeather
import com.example.superfitness.domain.repository.WeatherRepository

class WeatherRepositoryImpl (
    private val weatherApi: WeatherApi
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Double, long: Double): ApiWeather {
        val apiWeather = weatherApi.getWeatherData(
            latitude = lat,
            longitude = long
        )

        return apiWeather
    }
}

// To do:
// Save the latest location for weather