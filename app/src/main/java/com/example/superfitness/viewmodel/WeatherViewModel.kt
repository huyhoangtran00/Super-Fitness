package com.example.superfitness.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.common.Resource
import com.example.superfitness.domain.repository.IWeatherRepository
import com.example.superfitness.repository.ILocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: IWeatherRepository,
    private val locationTracker: ILocationTracker
): ViewModel() {
    private val TAG = "WeatherViewModel"
    var state by mutableStateOf(WeatherState())
        private set
    var stateForecastWeather by mutableStateOf(ForecastWeatherState())
        private set
    fun loadWeatherInfo(){
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            locationTracker.getCurrentLocation()?.let { location ->
                location.second?.let {
                    when (val result =
                        repository.getWeatherData(it.latitude, it.longitude)) {
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
                                address = null,
                                weatherInfo = null,
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            } ?: kotlin.run {
                state = state.copy(
                    address = null,
                    isLoading = false,
                    error = "Error permission"
                )
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
                    when (val result =
                        repository.getForecastWeatherData(it.latitude, it.longitude)) {
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

            } ?: run {
                stateForecastWeather = stateForecastWeather.copy(
                    isLoading = false,
                    error = "Error permission."
                )
            }
        }
    }
}