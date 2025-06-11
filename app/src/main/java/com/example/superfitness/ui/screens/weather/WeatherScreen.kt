package com.example.superfitness.ui.screens.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.R
import com.example.superfitness.data.di.AppViewModelProvider
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.screens.weather.components.ActionBar
import com.example.superfitness.ui.screens.weather.components.DailyForecast
import com.example.superfitness.ui.screens.weather.components.ErrorScreen
import com.example.superfitness.ui.screens.weather.components.HourlyForecast
import com.example.superfitness.ui.screens.weather.components.AdditionalWeatherData
import com.example.superfitness.ui.screens.weather.components.ThreeDayForecast
import com.example.superfitness.utils.GREEN

object WeatherDestination : NavigationDestination {
    override val route = "weather screen"
    override val titleRes = R.string.weather
}
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val weatherUiState = weatherViewModel.weatherState

    if (weatherUiState.isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(GREEN.toColorInt())
            )
        }
    } else {
        val weather = weatherUiState.weather
        if (weather != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 32.dp, start = 8.dp, end = 8.dp)
            ) {
                ActionBar(
                    modifier = Modifier.fillMaxWidth(),
                    address = weatherUiState.address ?: "Loading location..."
                )
                Spacer(modifier.height(16.dp))

                Text(
                    text = "Current Weather",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier.height(4.dp))

                DailyForecast(
                    modifier = Modifier.fillMaxWidth(),
                    currentWeather = weather.currentWeather
                )
                AdditionalWeatherData(
                    modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                    uvIndex = weather.hourlyWeather.weatherInfo.first().run {
                        this.uvIndex.toString()
                    },
                    humidity = weather.currentWeather.humidity.toString(),
                    windSpeed = weather.currentWeather.windSpeed.toString()
                )
                Spacer(modifier.height(8.dp))

                Text(
                    text = "Today 24-hour forecast",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier.height(8.dp))

                HourlyForecast(
                    modifier = Modifier.fillMaxWidth(),
                    hourlyWeather = weather.hourlyWeather
                )
                Spacer(modifier.height(16.dp))

                ThreeDayForecast(
                    modifier = Modifier.fillMaxWidth(),
                    dailyWeather = weather.dailyWeather
                )
            }
        } else {
            ErrorScreen(
                message = weatherUiState.error ?: "Something went wrong",
                onRetry = weatherViewModel::fetchWeatherFromCurrentLocation
            )
        }
    }
}