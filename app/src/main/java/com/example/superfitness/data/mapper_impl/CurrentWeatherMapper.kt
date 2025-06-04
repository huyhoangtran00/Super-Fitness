package com.example.superfitness.data.mapper_impl

import com.example.superfitness.data.mappers.ApiMapper
import com.example.superfitness.data.remote.models.ApiCurrentWeather
import com.example.superfitness.domain.models.CurrentWeather
import com.example.superfitness.utils.WeatherInfoItem
import com.example.superfitness.utils.WeatherUtils

class CurrentWeatherMapper : ApiMapper<CurrentWeather, ApiCurrentWeather> {
    override fun mapToDomain(apiEntity: ApiCurrentWeather): CurrentWeather {
        return CurrentWeather(
            temperature = apiEntity.temperature2m,
            time = parseTime(apiEntity.time),
            weatherStatus = parseWeatherStatus(apiEntity.weatherCode),
            windDirection = parseWindDirection(apiEntity.windDirection10m),
            windSpeed = apiEntity.windSpeed10m,
            isDay = apiEntity.isDay == 1
        )
    }

    private fun parseTime(time: Long): String {
        return WeatherUtils.formatUnixDate("MMM,d", time)
    }

    private fun parseWeatherStatus(code: Int): WeatherInfoItem {
        return WeatherUtils.getWeatherInfo(code)
    }

    private fun parseWindDirection(windDirection: Double): String {
        return WeatherUtils.getWindDirection(windDirection)
    }
}