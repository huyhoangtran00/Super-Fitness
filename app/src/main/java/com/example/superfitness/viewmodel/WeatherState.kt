package com.example.superfitness.viewmodel

import android.location.Address
import com.example.superfitness.domain.weather.ForecastWeatherData
import com.example.superfitness.domain.weather.ForecastWeatherInfo
import com.example.superfitness.domain.weather.WeatherInfo

data class WeatherState(
    val address: Address ?= null,
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ForecastWeatherState(
    val weatherInfoList: List<Pair<String,ForecastWeatherData>>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)