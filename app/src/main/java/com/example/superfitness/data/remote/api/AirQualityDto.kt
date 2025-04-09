package com.example.superfitness.data.remote.api

import com.squareup.moshi.Json

data class AirQualityDto(
    val hourly: AirQualityHourlyDto
)

data class AirQualityHourlyDto(
    val time: List<String>,
    @field:Json(name = "pm10")
    val pm10: List<Double>,
    @field:Json(name = "pm2_5")
    val pm2_5: List<Double>,
    @field:Json(name = "carbon_dioxide")
    val carbonDioxide: List<Double>
) 