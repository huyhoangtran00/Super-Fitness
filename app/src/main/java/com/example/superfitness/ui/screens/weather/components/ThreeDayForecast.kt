package com.example.superfitness.ui.screens.weather.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.superfitness.domain.models.DailyWeather
import com.example.superfitness.utils.BLUE
import com.example.superfitness.utils.WeatherUtils
import kotlin.math.roundToInt

@Composable
fun ThreeDayForecast(
    modifier: Modifier = Modifier,
    dailyWeather: DailyWeather
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth().background(
            color = Color.White,
            shape = RoundedCornerShape(32.dp)
        ),
    ) {
        dailyWeather.weatherInfo.take(3).forEachIndexed { index, item ->
            DailyForecastItem(
                dayOfTheWeek = when(index) {
                    0 -> "Today"
                    1 -> "Tomorrow"
                    else -> WeatherUtils.formatUnixDate("EEEE", item.time).take(3) },
                item = item
            )
            if (index != 2) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun DailyForecastItem(
    modifier: Modifier = Modifier,
    dayOfTheWeek: String,
    item: DailyWeather.WeatherInfo
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayOfTheWeek,
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.weight(1f)
        )
        WeatherImage(
            height = 32.dp,
            icon = item.weatherStatus.icon,
            modifier = Modifier.weight(1f)
        )
        DailyMaxAndMinTemperatures(
            max = item.temperatureMax.roundToInt().toString(),
            min = item.temperatureMin.roundToInt().toString(),
            rain = item.rainProbability.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DailyMaxAndMinTemperatures(
    modifier: Modifier = Modifier,
    max: String,
    min: String,
    rain: String
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = "$max°",
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$min°",
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$rain%",
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color(BLUE.toColorInt())
            )
        )
    }
}