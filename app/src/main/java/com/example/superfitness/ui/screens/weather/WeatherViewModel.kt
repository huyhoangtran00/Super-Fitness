package com.example.superfitness.ui.screens.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.domain.location.GeocoderHelper
import com.example.superfitness.domain.location.LocationManager
import com.example.superfitness.domain.models.DailyWeather
import com.example.superfitness.domain.models.Weather
import com.example.superfitness.domain.repository.WeatherRepository
import com.example.superfitness.utils.Response
import com.example.superfitness.utils.WeatherUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val locationManager: LocationManager,
    private val geocoderHelper: GeocoderHelper,
) : ViewModel() {

    var weatherState by mutableStateOf(WeatherUiState())
        private set
    init {
        fetchWeatherFromCurrentLocation()
    }

    private fun fetchWeatherFromCurrentLocation() {
        viewModelScope.launch {
            // Suspend till first location is emitted
            locationManager.locationUpdates.firstOrNull()?.let { location ->
                val address = geocoderHelper.getAddressFromLocation(location.latitude, location.longitude)
                repository.getWeatherData(
                    lat = location.latitude,
                    long = location.longitude
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            weatherState = weatherState.copy(isLoading = true)
                        }

                        is Response.Success -> {
                            val weather = response.data
                            val todayDailyWeatherInfo = weather?.dailyWeather?.weatherInfo?.find {
                                WeatherUtils.isTodayDate(WeatherUtils.formatUnixDate("E", it.time))
                            }

                            weatherState = weatherState.copy(
                                isLoading = false,
                                weather = weather,
                                dailyWeatherWeatherInfo = todayDailyWeatherInfo,
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
    val isLoading: Boolean = false,
    val dailyWeatherWeatherInfo: DailyWeather.WeatherInfo? = null,
    val address: String? = null
)