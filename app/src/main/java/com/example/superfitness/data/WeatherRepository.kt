package com.example.superfitness.data

import android.util.Log
import com.example.superfitness.common.Resource
import com.example.superfitness.data.mapper.toForecastWeatherDataMap
import com.example.superfitness.data.mapper.toForecastWeatherInfo
import com.example.superfitness.data.mapper.toWeatherInfo
import com.example.superfitness.data.remote.api.WeatherApi
import com.example.superfitness.domain.repository.IWeatherRepository
import com.example.superfitness.domain.weather.ForecastWeatherInfo
import com.example.superfitness.domain.weather.WeatherInfo
import javax.inject.Inject


class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
): IWeatherRepository {
    private val TAG = "WeatherRepositoryImpl"
    override suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo> {
        return try {
            val result  = api.getWeatherData(
                lat = lat,
                long = long
            )
            Resource.Success(
                result.toWeatherInfo()
            )
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun getForecastWeatherData(lat: Double, long: Double): Resource<ForecastWeatherInfo> {
        return try {
            val result = api.getForecastWeatherData(
                lat = lat,
                long = long
            ).toForecastWeatherInfo()
            Log.d(TAG, "getForecastWeatherData: ====> result = $result")
            Resource.Success(
                data = result
            )
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
}