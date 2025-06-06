package com.example.superfitness.domain.models

import com.example.superfitness.utils.WeatherInfoItem

data class DailyWeather(
    private val temperatureMax: List<Double>,
    private val temperatureMin: List<Double>,
    private val rainSum: List<Double>,
    private val time: List<Long>,
    private val weatherStatus: List<WeatherInfoItem>,
    private val windDirection: List<String>,
    private val windSpeed: List<Double>,
    private val sunrise: List<String>,
    private val sunset: List<String>,
    private val uvIndex: List<Double>,
    private val rainProbability: List<Int>
) {
    val weatherInfo:List<WeatherInfo>
        get() {
            val dailyWeatherInfo = mutableListOf<WeatherInfo>()
            temperatureMin.indices.forEach{ i ->
                dailyWeatherInfo.add(
                    WeatherInfo(
                        temperatureMax = temperatureMax[i],
                        temperatureMin = temperatureMin[i],
                        rainSum = rainSum[i],
                        time = time[i],
                        weatherStatus = weatherStatus[i],
                        windDirection = windDirection[i],
                        windSpeed = windSpeed[i],
                        sunrise = sunrise[i],
                        sunset = sunset[i],
                        uvIndex = uvIndex[i],
                        rainProbability = rainProbability[i]
                    )
                )
            }
            return dailyWeatherInfo
        }
    data class WeatherInfo(
        val temperatureMax: Double,
        val temperatureMin: Double,
        val rainSum: Double,
        val time: Long,
        val weatherStatus: WeatherInfoItem,
        val windDirection: String,
        val windSpeed: Double,
        val sunrise: String,
        val sunset: String,
        val uvIndex: Double,
        val rainProbability: Int
    )
}