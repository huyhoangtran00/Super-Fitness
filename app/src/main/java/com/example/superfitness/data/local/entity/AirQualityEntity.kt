package com.example.superfitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.superfitness.domain.weather.AirQualityData

@Entity(tableName = "air_quality")
data class AirQualityEntity(
    @PrimaryKey
    val id: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val pm10: Double,
    val pm2_5: Double,
    val carbonDioxide: Double,
    val latitude: Double,
    val longitude: Double
) {
    fun toAirQualityData(): AirQualityData {
        return AirQualityData(
            pm10 = pm10,
            pm2_5 = pm2_5,
            carbonDioxide = carbonDioxide,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromAirQualityData(data: AirQualityData, lat: Double, long: Double): AirQualityEntity {
            return AirQualityEntity(
                pm10 = data.pm10,
                pm2_5 = data.pm2_5,
                carbonDioxide = data.carbonDioxide,
                latitude = lat,
                longitude = long
            )
        }
    }
} 