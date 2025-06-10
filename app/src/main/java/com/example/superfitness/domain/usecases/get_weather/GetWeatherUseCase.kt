package com.example.superfitness.domain.usecases.get_weather

import com.example.superfitness.data.mapper_impl.ApiWeatherMapper
import com.example.superfitness.domain.models.Weather
import com.example.superfitness.domain.repository.WeatherRepository
import com.example.superfitness.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetWeatherUseCase(
    private val repository: WeatherRepository,
    private val apiWeatherMapper: ApiWeatherMapper
) {
    operator fun invoke(lat: Double, long: Double): Flow<Response<Weather>> = flow {
        try {
            emit(Response.Loading<Weather>())
            val apiWeather = repository.getWeatherData(lat, long)
            val weather = apiWeatherMapper.mapToDomain(apiWeather)
            emit(Response.Success<Weather>(weather))
        } catch(e: IOException) {
            e.printStackTrace()
            emit(Response.Error(e.message.orEmpty()))
        }
    }
}