package com.example.superfitness.domain.repository

import com.example.superfitness.domain.models.Weather
import com.example.superfitness.utils.Response
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherData(): Flow<Response<Weather>>
}