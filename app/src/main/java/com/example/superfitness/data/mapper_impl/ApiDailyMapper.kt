package com.example.superfitness.data.mapper_impl

import com.example.superfitness.data.mappers.ApiMapper
import com.example.superfitness.data.remote.models.ApiDailyWeather
import com.example.superfitness.domain.models.Daily
import com.example.superfitness.utils.WeatherInfoItem
import com.example.superfitness.utils.WeatherUtils

class ApiDailyMapper : ApiMapper<Daily, ApiDailyWeather> {
    override fun mapToDomain(apiEntity: ApiDailyWeather): Daily {
        return Daily(
            temperatureMax = apiEntity.temperature2mMax,
            temperatureMin = apiEntity.temperature2mMin,
            time = parseTime(apiEntity.time),
            weatherStatus = parseWeatherStatus(apiEntity.weatherCode),
            windDirection = parseWeatherDirection(apiEntity.windDirection10mDominant),
            sunset = apiEntity.sunset.map { WeatherUtils.formatUnixDate("HH:mm", it.toLong()) },
            sunrise = apiEntity.sunrise.map { WeatherUtils.formatUnixDate("HH:mm", it.toLong()) },
            uvIndex = apiEntity.uvIndexMax,
            windSpeed = apiEntity.windSpeed10mMax
        )
    }

    private fun parseTime(time: List<Long>): List<String> {
        return time.map { WeatherUtils.formatUnixDate("E", it) }
    }

    private fun parseWeatherStatus(code: List<Int>): List<WeatherInfoItem> {
        return code.map {
            WeatherUtils.getWeatherInfo(it)
        }
    }

    private fun parseWeatherDirection(windDirections: List<Double>): List<String> {
        return windDirections.map {
            WeatherUtils.getWindDirection(it)
        }
    }

}