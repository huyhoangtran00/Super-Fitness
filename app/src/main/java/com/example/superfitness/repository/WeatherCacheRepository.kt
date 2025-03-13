package com.example.superfitness.repository

import androidx.lifecycle.LiveData
import com.example.superfitness.data.local.db.dao.WeatherCacheDao
import com.example.superfitness.data.local.db.entity.WeatherCache

class WeatherCacheRepository(private val weatherCacheDao: WeatherCacheDao) {

    // Lấy tất cả thông tin thời tiết
    fun getAllWeatherCache(): LiveData<List<WeatherCache>> = weatherCacheDao.getAllWeatherCache()

    // Thêm thông tin thời tiết
    suspend fun insertWeatherCache(weatherCache: WeatherCache) {
        weatherCacheDao.insert(weatherCache)
    }

    // Lấy thông tin thời tiết theo thành phố
    suspend fun getWeatherByCity(city: String): WeatherCache? {
        return weatherCacheDao.getWeatherByCity(city)
    }

    // Xóa thông tin thời tiết
    suspend fun deleteWeatherCache(weatherCache: WeatherCache) {
        weatherCacheDao.delete(weatherCache)
    }
}
