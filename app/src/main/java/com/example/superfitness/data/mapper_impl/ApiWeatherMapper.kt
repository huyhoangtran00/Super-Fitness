package com.example.superfitness.data.mapper_impl

import com.example.superfitness.data.mappers.ApiMapper
import com.example.superfitness.data.remote.models.ApiCurrentWeather
import com.example.superfitness.data.remote.models.ApiDailyWeather
import com.example.superfitness.data.remote.models.ApiHourlyWeather
import com.example.superfitness.data.remote.models.ApiWeather
import com.example.superfitness.domain.models.CurrentWeather
import com.example.superfitness.domain.models.Daily
import com.example.superfitness.domain.models.Hourly
import com.example.superfitness.domain.models.Weather

class ApiWeatherMapper(
    private val apiDailyMapper: ApiMapper<Daily, ApiDailyWeather>,
    private val apiCurrentWeatherMapper: ApiMapper<CurrentWeather, ApiCurrentWeather>,
    private val apiHourlyMapper: ApiMapper<Hourly, ApiHourlyWeather>,
) : ApiMapper<Weather, ApiWeather> {
    override fun mapToDomain(apiEntity: ApiWeather): Weather {
        return Weather(
            currentWeather = apiCurrentWeatherMapper.mapToDomain(apiEntity.current),
            daily = apiDailyMapper.mapToDomain(apiEntity.daily),
            hourly = apiHourlyMapper.mapToDomain(apiEntity.hourly)
        )
    }
}