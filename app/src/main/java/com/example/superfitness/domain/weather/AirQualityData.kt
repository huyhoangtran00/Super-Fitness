package com.example.superfitness.domain.weather

data class AirQualityData(
    val pm10: Double,
    val pm2_5: Double,
    val carbonDioxide: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class AirQualityInfo(
    val currentAirQuality: AirQualityData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) 