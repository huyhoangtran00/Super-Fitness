package com.example.superfitness.ui.screens.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.superfitness.domain.models.HourlyWeather
import com.example.superfitness.utils.BLUE
import kotlin.math.roundToInt

@Composable
fun HourlyForecast(
    modifier: Modifier = Modifier,
    hourlyWeather: HourlyWeather
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hourlyWeather.weatherInfo) { item ->
            HourlyForecastItem(
                item = item
            )
        }
    }
}

@Composable
private fun HourlyForecastItem(
    modifier: Modifier = Modifier,
    item: HourlyWeather.HourlyInfoItem
) {
    Card(
        modifier = Modifier.width(80.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color("#82c8e5".toColorInt()))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            WeatherImage(
                modifier = Modifier.fillMaxWidth(),
                icon = item.weatherStatus.icon,
                height = 32.dp
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Text(
                text = item.temperature.roundToInt().toString() + "°",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            RainIndicator(
                value = item.rainProbability.toString()
            )
        }
    }
}

@Composable
private fun RainIndicator(
    modifier: Modifier = Modifier,
    value: String
) {
    Surface(
        modifier = modifier,
        color = Color(BLUE.toColorInt()),
        contentColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$value%",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}