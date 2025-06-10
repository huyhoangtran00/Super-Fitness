package com.example.superfitness.data.repository

import com.example.superfitness.data.mapper_impl.ApiWeatherMapper
import com.example.superfitness.data.remote.WeatherApi
import com.example.superfitness.data.remote.models.ApiWeather
import com.example.superfitness.domain.models.Weather
import com.example.superfitness.domain.repository.WeatherRepository
import com.example.superfitness.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl (
    private val weatherApi: WeatherApi,
    //private val apiWeatherMapper: ApiWeatherMapper
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Double, long: Double): ApiWeather {
        val apiWeather = weatherApi.getWeatherData(
            latitude = lat,
            longitude = long
        )

        return apiWeather
    }
//    Flow<Response<Weather>> = flow {
//        emit(Response.Loading())
//        val apiWeather = weatherApi.getWeatherData(
//            latitude = lat,
//            longitude = long
//        )
//        val weather = apiWeatherMapper.mapToDomain(apiWeather)
//        emit(Response.Success(weather))
//    }.catch { e ->
//        e.printStackTrace()
//        emit(Response.Error(e.message.orEmpty()))
//    }
}

// To do:
// Save the latest location for weather