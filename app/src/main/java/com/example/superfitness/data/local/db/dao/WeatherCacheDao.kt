package com.example.superfitness.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.superfitness.data.local.db.entity.WeatherCache

@Dao
interface WeatherCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherCache: WeatherCache) : Long

    @Update
    suspend fun update(weatherCache: WeatherCache): Int

    @Delete
    suspend fun delete(weatherCache: WeatherCache): Int

    @Query("SELECT * FROM weather_cache WHERE city = :city")
    suspend fun getWeatherByCity(city: String): WeatherCache?

    @Query("SELECT * FROM weather_cache")
    fun getAllWeatherCache(): LiveData<List<WeatherCache>>
}
