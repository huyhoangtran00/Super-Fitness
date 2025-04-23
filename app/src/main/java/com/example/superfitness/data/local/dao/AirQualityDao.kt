package com.example.superfitness.data.local.dao

import androidx.room.*
import com.example.superfitness.data.local.entity.AirQualityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AirQualityDao {
    @Query("SELECT * FROM air_quality WHERE id = 1")
    fun getAirQuality(): Flow<AirQualityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirQuality(airQuality: AirQualityEntity)

    @Query("DELETE FROM air_quality")
    suspend fun deleteAirQuality()
} 