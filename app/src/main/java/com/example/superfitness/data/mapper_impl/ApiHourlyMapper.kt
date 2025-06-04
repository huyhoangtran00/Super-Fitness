package com.example.superfitness.data.mapper_impl

import com.example.superfitness.data.mappers.ApiMapper
import com.example.superfitness.data.remote.models.ApiHourlyWeather
import com.example.superfitness.domain.models.Hourly
import com.example.superfitness.utils.WeatherInfoItem
import com.example.superfitness.utils.WeatherUtils

class ApiHourlyMapper : ApiMapper<Hourly, ApiHourlyWeather> {
    override fun mapToDomain(apiEntity: ApiHourlyWeather): Hourly {
        return Hourly(
            temperature = apiEntity.temperature2m,
            time = parseTime(apiEntity.time),
            weatherStatus = parseWeatherStatus(apiEntity.weatherCode)
        )
    }

    private fun parseTime(time: List<Long>): List<String> {
        return time.map {
            WeatherUtils.formatUnixDate("HH:mm", it)
        }
    }

    private fun parseWeatherStatus(code: List<Int>): List<WeatherInfoItem> {
        return code.map {
            WeatherUtils.getWeatherInfo(it)
        }
    }


}