package com.example.superfitness.ui.screens.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.R
import com.example.superfitness.domain.models.CurrentWeather
import com.example.superfitness.domain.models.Hourly
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.utils.WeatherUtils
import com.example.superfitness.viewmodel.AppViewModelProvider
import java.util.Date

const val degreeText = "\u0000"

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
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when(weatherUiState.isLoading) {
            true -> {
                CircularProgressIndicator()
            }
            else -> {
                weatherUiState.weather?.let {
                    CurrentWeatherItem(
                        modifier = Modifier.align(Alignment.TopCenter),
                        currentWeather = it.currentWeather
                    )
                    HourlyWeatherItem(
                        hourly = it.hourly,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherItem(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeather
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = currentWeather.weatherStatus.icon),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = currentWeather.temperature.toString() + degreeText,
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Weather Status: ${currentWeather.weatherStatus.info}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Weather Status: ${currentWeather.windDirection} ${currentWeather.windSpeed} Km/h",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun HourlyWeatherItem(
    modifier: Modifier = Modifier,
    hourly: Hourly,
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = WeatherUtils.formatNormalDate("MMMM,dd", Date().time),
                style = MaterialTheme.typography.bodyMedium,
            )

        }
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(
            modifier = Modifier.padding(16.dp)
        ) {
            items(hourly.weatherInfo) { infoItem ->
                HourlyWeatherInfoItem(infoItem = infoItem)
            }
        }
    }
}

@Composable
fun HourlyWeatherInfoItem(
    modifier: Modifier = Modifier,
    infoItem: Hourly.HourlyInfoItem,
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${infoItem.temperature} $degreeText",
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
            painter = painterResource(id = infoItem.weatherStatus.icon),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = infoItem.time,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}