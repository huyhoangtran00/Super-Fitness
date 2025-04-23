package com.example.superfitness.domain.repository

import com.example.superfitness.common.Resource
import com.example.superfitness.domain.weather.AirQualityData

interface IAirQualityRepository {
    suspend fun getAirQualityData(lat: Double, long: Double): Resource<AirQualityData>
    suspend fun getCachedAirQualityData(lat: Double, long: Double): AirQualityData?
    suspend fun cacheAirQualityData(data: AirQualityData, lat: Double, long: Double)
} 