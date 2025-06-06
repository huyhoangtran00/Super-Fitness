package com.example.superfitness.data.mapper_impl

import com.example.superfitness.data.mappers.ApiMapper
import com.example.superfitness.data.remote.models.ApiDailyWeather
import com.example.superfitness.domain.models.DailyWeather
import com.example.superfitness.utils.WeatherInfoItem
import com.example.superfitness.utils.WeatherUtils

class ApiDailyMapper : ApiMapper<DailyWeather, ApiDailyWeather> {
    override fun mapToDomain(apiEntity: ApiDailyWeather): DailyWeather {
        return DailyWeather(
            temperatureMax = apiEntity.temperature2mMax,
            temperatureMin = apiEntity.temperature2mMin,
            time = apiEntity.time,
            weatherStatus = parseWeatherStatus(apiEntity.weatherCode),
            windDirection = parseWeatherDirection(apiEntity.windDirection10mDominant),
            sunset = apiEntity.sunset.map { WeatherUtils.formatUnixDate("HH:mm", it.toLong()) },
            sunrise = apiEntity.sunrise.map { WeatherUtils.formatUnixDate("HH:mm", it.toLong()) },
            uvIndex = apiEntity.uvIndexMax,
            windSpeed = apiEntity.windSpeed10mMax,
            rainSum = apiEntity.rainSum,
            rainProbability = apiEntity.rainProbability
        )
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