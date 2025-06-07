package com.example.superfitness.data.repository

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.superfitness.common.Resource
import com.example.superfitness.data.local.dao.WeatherDao
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.local.entity.ForecastWeatherEntity
import com.example.superfitness.data.local.entity.WeatherEntity
import com.example.superfitness.data.mapper.toForecastWeatherInfo
import com.example.superfitness.data.mapper.toWeatherInfo
import com.example.superfitness.data.remote.api.WeatherApi
import com.example.superfitness.domain.weather.ForecastWeatherInfo
import com.example.superfitness.domain.weather.WeatherInfo
import com.example.superfitness.domain.repository.IWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class WeatherRepository (
    private val api: WeatherApi,
    private val dao: WeatherDao,
    private val connectivityManager: ConnectivityManager
) : IWeatherRepository {
    private val TAG=  "WeatherRepository"

    private val CACHE_DURATION = 30 * 60 * 1000 // 30 minutes in milliseconds

    @SuppressLint("NewApi")
    override suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo> {
        return try {
            if (isNetworkAvailable()) {
                try {
                    val response = api.getWeatherData(lat, long)
                    val weatherInfo = response.toWeatherInfo()
                    
                    // Cache the fresh data
                    cacheWeatherInfo(weatherInfo)
                    
                    return Resource.Success(weatherInfo)
                } catch (e: Exception) {
                    // If API call fails, try to get cached data
                    val cachedData = getCachedWeatherInfo()
                    if (cachedData != null) {
                        return Resource.Success(cachedData)
                    }
                    throw e
                }
            } else {
                // If offline, use cached data
                val cachedData = getCachedWeatherInfo()
                if (cachedData != null) {
                    return Resource.Success(cachedData)
                }
                return Resource.Error("No internet connection and no cached data available")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getForecastWeatherData(lat: Double, long: Double): Resource<ForecastWeatherInfo> {
        return try {
            if (isNetworkAvailable()) {
                try {
                    val response = api.getForecastWeatherData(lat, long)
                    val forecastWeatherInfo = response.toForecastWeatherInfo()
                    
                    // Cache the fresh data
                    cacheForecastWeatherInfo(forecastWeatherInfo, lat, long)
                    
                    return Resource.Success(forecastWeatherInfo)
                } catch (e: Exception) {
                    // If API call fails, try to get cached data
                    val cachedData = getCachedForecastWeatherInfo(lat, long)
                    Log.d(TAG, "getForecastWeatherData: ====> cachedData = $cachedData")
                    if (cachedData != null) {
                        return Resource.Success(cachedData)
                    }
                    throw e
                }
            } else {
                // If offline, use cached data
                val cachedData = getCachedForecastWeatherInfo(lat, long)
                Log.d(TAG, "getForecastWeatherData: ====> cachedData = $cachedData")
                if (cachedData != null) {
                    return Resource.Success(cachedData)
                }
                return Resource.Error("No internet connection and no cached data available")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    private suspend fun getCachedWeatherInfo(): WeatherInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val weatherEntity = dao.getWeather().first()
                Log.d(TAG, "getCachedWeatherInfo: ====> data = $weatherEntity")
                weatherEntity?.let { entity ->
                    if (System.currentTimeMillis() - entity.timestamp < CACHE_DURATION) {
                        entity.toWeatherInfo()
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getCachedWeatherInfo: ====>ererrror = ${e.message}")
                null
            }
        }
    }

    private suspend fun getCachedForecastWeatherInfo(lat: Double, long: Double): ForecastWeatherInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val forecastWeatherEntity = dao.getForecastWeather().first()
                Log.d(TAG, "getCachedForecastWeatherInfo: ====> data = $forecastWeatherEntity")
                forecastWeatherEntity?.let { entity ->
                    entity.toForecastWeatherInfo()
                }
            } catch (e: Exception) {
                Log.d(TAG, "getCachedForecastWeatherInfo: ====>ererrror = ${e.message}")
                null
            }
        }
    }

    private suspend fun cacheWeatherInfo(weatherInfo: WeatherInfo) {
        withContext(Dispatchers.IO) {
            try {
                dao.insertWeather(WeatherEntity.fromWeatherInfo(weatherInfo))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private suspend fun cacheForecastWeatherInfo(forecastWeatherInfo: ForecastWeatherInfo, lat: Double, long: Double) {
        withContext(Dispatchers.IO) {
            try {
                dao.insertForecastWeather(ForecastWeatherEntity.fromForecastWeatherInfo(forecastWeatherInfo, lat, long))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun isLocationMatch(entity: ForecastWeatherEntity, lat: Double, long: Double): Boolean {
        val latDiff = Math.abs(entity.latitude - lat)
        val longDiff = Math.abs(entity.longitude - long)
        return latDiff < 1.0 && longDiff < 1.0
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
} 