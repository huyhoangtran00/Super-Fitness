package com.example.superfitness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.superfitness.repository.WeatherCacheRepository
import com.example.superfitness.data.local.db.entity.WeatherCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherCacheViewModel(private val weatherCacheRepository: WeatherCacheRepository) : ViewModel() {

    // Lấy tất cả thông tin thời tiết từ repository
    fun getAllWeatherCache(): LiveData<List<WeatherCache>> {
        return weatherCacheRepository.getAllWeatherCache()
    }

    // Thêm thông tin thời tiết vào repository
    fun insertWeatherCache(weatherCache: WeatherCache) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherCacheRepository.insertWeatherCache(weatherCache)
        }
    }

    // Lấy thông tin thời tiết theo thành phố
    fun getWeatherByCity(city: String): LiveData<WeatherCache?> {
        return liveData(Dispatchers.IO) {
            emit(weatherCacheRepository.getWeatherByCity(city))
        }
    }

    // Xóa thông tin thời tiết
    fun deleteWeatherCache(weatherCache: WeatherCache) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherCacheRepository.deleteWeatherCache(weatherCache)
        }
    }
}
