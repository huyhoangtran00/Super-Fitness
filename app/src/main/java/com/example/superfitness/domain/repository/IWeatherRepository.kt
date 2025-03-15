package com.example.superfitness.domain.repository

import com.example.superfitness.common.Resource
import com.example.superfitness.domain.weather.ForecastWeatherInfo
import com.example.superfitness.domain.weather.WeatherInfo

interface IWeatherRepository {
    suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo>
    suspend fun getForecastWeatherData(lat: Double, long: Double) : Resource<ForecastWeatherInfo>
}