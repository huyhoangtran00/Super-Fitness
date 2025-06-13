package com.example.superfitness.ui.screens.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.domain.connectivity.ConnectivityObserver
import com.example.superfitness.domain.location.GeocoderHelper
import com.example.superfitness.domain.location.LocationManager
import com.example.superfitness.domain.models.Weather
import com.example.superfitness.domain.usecases.weather.GetWeatherUseCase
import com.example.superfitness.utils.Response
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val locationManager: LocationManager,
    private val geocoderHelper: GeocoderHelper,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    var weatherState by mutableStateOf(WeatherUiState())
        private set

    init {
        fetchWeatherFromCurrentLocation()
    }

    fun fetchWeatherFromCurrentLocation() {
        viewModelScope.launch {
            // Every time fetching, show loading circular
            weatherState = weatherState.copy(
                isLoading = true
            )

            // Check Internet Connectivity
            if (!connectivityObserver.isConnected.first()) {
                weatherState = weatherState.copy(
                    isLoading = false,
                    error = "Check Internet connection"
                )
                return@launch
            }

            // Check Gps
            val location = withTimeoutOrNull(3000) {
                locationManager.locationUpdates.firstOrNull()
            }

            if (location == null) {
                weatherState = weatherState.copy(
                    isLoading = false,
                    error = "Unable to fetch location\nPlease check GPS settings"
                )
                return@launch
            } else {
                val address = geocoderHelper.getAddressFromLocation(location.latitude, location.longitude)

                getWeatherUseCase(
                    lat = location.latitude,
                    long = location.longitude
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            weatherState = weatherState.copy(isLoading = true)
                        }

                        is Response.Success -> {
                            val weather = response.data

                            weatherState = weatherState.copy(
                                isLoading = false,
                                weather = weather,
                                address = address,
                                error = null
                            )
                        }

                        is Response.Error -> {
                            weatherState = weatherState.copy(
                                isLoading = false,
                                error = response.message
                            )
                        }
                    }
                }
            }
        }
    }
}

data class WeatherUiState(
    val weather: Weather? = null,
    val error: String? = null,
    val address: String? = null,
    val isLoading: Boolean = false
)