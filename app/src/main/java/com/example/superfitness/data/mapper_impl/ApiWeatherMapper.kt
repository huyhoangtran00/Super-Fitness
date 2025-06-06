package com.example.superfitness.data.mapper_impl

import com.example.superfitness.data.mappers.ApiMapper
import com.example.superfitness.data.remote.models.ApiCurrentWeather
import com.example.superfitness.data.remote.models.ApiDailyWeather
import com.example.superfitness.data.remote.models.ApiHourlyWeather
import com.example.superfitness.data.remote.models.ApiWeather
import com.example.superfitness.domain.models.CurrentWeather
import com.example.superfitness.domain.models.DailyWeather
import com.example.superfitness.domain.models.HourlyWeather
import com.example.superfitness.domain.models.Weather

class ApiWeatherMapper(
    private val apiDailyWeatherMapper: ApiMapper<DailyWeather, ApiDailyWeather>,
    private val apiCurrentWeatherMapper: ApiMapper<CurrentWeather, ApiCurrentWeather>,
    private val apiHourlyWeatherMapper: ApiMapper<HourlyWeather, ApiHourlyWeather>,
) : ApiMapper<Weather, ApiWeather> {
    override fun mapToDomain(apiEntity: ApiWeather): Weather {
        return Weather(
            currentWeather = apiCurrentWeatherMapper.mapToDomain(apiEntity.current),
            dailyWeather = apiDailyWeatherMapper.mapToDomain(apiEntity.daily),
            hourlyWeather = apiHourlyWeatherMapper.mapToDomain(apiEntity.hourly)
        )
    }
}