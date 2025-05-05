package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.superfitness.domain.repository.IAirQualityRepository
import com.example.superfitness.domain.repository.IWeatherRepository
import com.example.superfitness.repository.ILocationTracker

class WeatherViewModelFactory (private val repository: IWeatherRepository,
                               private val airQualityRepository: IAirQualityRepository,
                               private val locationTracker: ILocationTracker
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra nếu modelClass là WaterIntakeViewModel
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository, airQualityRepository, locationTracker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
