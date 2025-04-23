package com.example.superfitness.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.common.Resource
import com.example.superfitness.domain.repository.IAirQualityRepository
import com.example.superfitness.domain.repository.IWeatherRepository
import com.example.superfitness.domain.weather.AirQualityInfo
import com.example.superfitness.repository.ILocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherViewModel (
    private val repository: IWeatherRepository,
    private val airQualityRepository: IAirQualityRepository,
    private val locationTracker: ILocationTracker
): ViewModel() {
    private val TAG = "WeatherViewModel"
    var state by mutableStateOf(WeatherState())
        private set
    var stateForecastWeather by mutableStateOf(ForecastWeatherState())
        private set
    var airQualityState by mutableStateOf(AirQualityInfo())
        private set

    fun loadWeatherInfo(){
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            locationTracker.getCurrentLocation()?.let { location ->
                location.second?.let {
                    when (val result = repository.getWeatherData(it.latitude, it.longitude)) {
                        is Resource.Success -> {
                            state = state.copy(
                                address = location.first,
                                weatherInfo = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadAirQualityInfo() {
        viewModelScope.launch {
            airQualityState = airQualityState.copy(isLoading = true, error = null)
            
            locationTracker.getCurrentLocation()?.let { location ->
                location.second?.let { loc ->
                    when (val result = airQualityRepository.getAirQualityData(loc.latitude, loc.longitude)) {
                        is Resource.Success -> {
                            airQualityState = airQualityState.copy(
                                currentAirQuality = result.data,
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            airQualityState = airQualityState.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadForecastWeatherInfo() {
        viewModelScope.launch {
            stateForecastWeather = stateForecastWeather.copy(
                isLoading = true,
                error = null
            )
            locationTracker.getCurrentLocation()?.let { location ->
                location.second?.let {
                    when (val result = repository.getForecastWeatherData(it.latitude, it.longitude)) {
                        is Resource.Success -> {
                            stateForecastWeather = stateForecastWeather.copy(
                                weatherInfoList = result.data?.weatherDataPerDay,
                                isLoading = false,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            stateForecastWeather = stateForecastWeather.copy(
                                weatherInfoList = null,
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}