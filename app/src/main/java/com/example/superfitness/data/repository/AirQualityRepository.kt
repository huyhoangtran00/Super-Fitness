package com.example.superfitness.data.repository

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.superfitness.common.Resource
import com.example.superfitness.data.local.dao.AirQualityDao
import com.example.superfitness.data.local.entity.AirQualityEntity
import com.example.superfitness.data.remote.api.AirQualityApi
import com.example.superfitness.domain.weather.AirQualityData
import com.example.superfitness.domain.repository.IAirQualityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class AirQualityRepository (
    private val api: AirQualityApi,
    private val dao: AirQualityDao,
    private val connectivityManager: ConnectivityManager
) : IAirQualityRepository {
    private val TAG=  "AirQualityRepository"
    private val CACHE_DURATION = 30 * 60 * 1000 // 30 minutes in milliseconds

    @SuppressLint("NewApi")
    override suspend fun getAirQualityData(lat: Double, long: Double): Resource<AirQualityData> {
        return try {
            // Check if we have internet connection
            if (isNetworkAvailable()) {
                // If online, always try to get fresh data first
                try {
                    val response = api.getAirQualityData(lat, long)
                    val currentHourIndex = LocalDateTime.now().hour
                    
                    val airQualityData = AirQualityData(
                        pm10 = response.hourly.pm10[currentHourIndex],
                        pm2_5 = response.hourly.pm2_5[currentHourIndex],
                        carbonDioxide = response.hourly.carbonDioxide[currentHourIndex]
                    )
                    
                    // Cache the fresh data
                    cacheAirQualityData(airQualityData, lat, long)
                    
                    return Resource.Success(airQualityData)
                } catch (e: Exception) {
                    // If API call fails, try to get cached data
                    val cachedData = getCachedAirQualityData(lat, long)
                    if (cachedData != null) {
                        return Resource.Success(cachedData)
                    }
                    throw e
                }
            } else {
                // If offline, use cached data
                val cachedData = getCachedAirQualityData(lat, long)
                Log.d(TAG, "getAirQualityData: ===> cachedData = $cachedData")
                if (cachedData != null) {
                    return Resource.Success(cachedData)
                }
                return Resource.Error("No internet connection and no cached data available")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getCachedAirQualityData(lat: Double, long: Double): AirQualityData? {
        return withContext(Dispatchers.IO) {
            try {
                val airQualityEntity = dao.getAirQuality().first()
                airQualityEntity?.let { entity ->
                    // Check if the cached data is for the same location and not too old
                    if (isLocationMatch(entity, lat, long)) {
                        entity.toAirQualityData()
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun cacheAirQualityData(data: AirQualityData, lat: Double , long: Double) {
        withContext(Dispatchers.IO) {
            try {
                dao.insertAirQuality(AirQualityEntity.fromAirQualityData(data, lat, long))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun isLocationMatch(entity: AirQualityEntity, lat: Double, long: Double): Boolean {
        // Consider locations within 1 degree as the same (approximately 111 km)
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