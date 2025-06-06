package com.example.superfitness.domain.models

import com.example.superfitness.utils.WeatherInfoItem

data class HourlyWeather(
    private val temperature: List<Double>,
    private val time: List<String>,
    private val weatherStatus: List<WeatherInfoItem>,
    private val humidity: List<Int>,
    private val rain: List<Double>,
    private val uvIndex: List<Double>,
    private val rainProbability: List<Int>
) {
    val weatherInfo: List<HourlyInfoItem>
        get() {
            return time.mapIndexed { index, time ->
                HourlyInfoItem(
                    temperature = temperature[index],
                    time = time,
                    weatherStatus = weatherStatus[index],
                    humidity = humidity[index],
                    rain = rain[index],
                    uvIndex = uvIndex[index],
                    rainProbability = rainProbability[index]
                )
            }
        }

    data class HourlyInfoItem(
        val temperature: Double,
        val time: String,
        val weatherStatus: WeatherInfoItem,
        val humidity:Int,
        val rain: Double,
        val uvIndex: Double,
        val rainProbability: Int
    )
}