package com.example.superfitness.ui.screens.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.domain.models.Daily
import com.example.superfitness.domain.models.Weather
import com.example.superfitness.domain.repository.WeatherRepository
import com.example.superfitness.utils.Response
import com.example.superfitness.utils.WeatherUtils
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    var weatherState by mutableStateOf(WeatherUiState())
        private set

    init {
        viewModelScope.launch {
            repository.getWeatherData().collect { response ->
                when (response) {
                    is Response.Loading -> {
                        weatherState = weatherState.copy(
                            isLoading = true
                        )
                    }

                    is Response.Success -> {
                        weatherState = weatherState.copy(
                            isLoading = false,
                            error = null,
                            weather = response.data
                        )
                        val todayDailWeatherInfo = response.data?.daily?.weatherInfo?.find {
                            WeatherUtils.isTodayDate(it.time)
                        }
                        weatherState = weatherState.copy(
                            dailyWeatherInfo = todayDailWeatherInfo
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

data class WeatherUiState(
    val weather: Weather? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val dailyWeatherInfo: Daily.WeatherInfo? = null
)