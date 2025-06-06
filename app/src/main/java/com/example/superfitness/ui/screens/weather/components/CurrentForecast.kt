package com.example.superfitness.ui.screens.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.superfitness.domain.models.CurrentWeather
import kotlin.math.roundToInt

@Composable
fun DailyForecast(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeather
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (forecastImage, forecastValue, title, background) = createRefs()

        CardBackground(
            modifier = Modifier.constrainAs(background) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = title.bottom,
                    topMargin = 24.dp,
                )
                height = Dimension.fillToConstraints
            },
            isDay = currentWeather.isDay
        )

        Icon(
            painter = painterResource(currentWeather.weatherStatus.icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .height(128.dp)
                .constrainAs(forecastImage) {
                    start.linkTo(anchor = parent.start, margin = 24.dp)
                    top.linkTo(parent.top)
                }
        )

        Text(
            text = currentWeather.weatherStatus.info,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            ),

            modifier = Modifier.constrainAs(title) {
                start.linkTo(anchor = parent.start, margin = 24.dp)
                top.linkTo(anchor = forecastImage.bottom, margin = 8.dp)
            }.padding(bottom = 16.dp)
        )


        ForecastValue(
            modifier = Modifier.constrainAs(forecastValue) {
                end.linkTo(anchor = parent.end, margin = 16.dp)
                top.linkTo(anchor = parent.top, margin = 24.dp)
            },
            degree = "${currentWeather.temperature.roundToInt()}°",
            description = "Feel like ${currentWeather.apparentTemperature.roundToInt()}°",
        )
    }
}

@Composable
private fun CardBackground(
    modifier: Modifier = Modifier,
    isDay: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush =
                    if (isDay) {
                        Brush.linearGradient(
                            0f to Color(0xFFF94D00),
                            0.5f to Color(0xFFFF8243),
                            1f to Color(0xFFFDEE00)
                        )
                    } else {
                        Brush.linearGradient(
                            0f to Color(0xFF0D324D),
                            1f to Color(0xFF7F5A83)
                        )
                    },
                shape = RoundedCornerShape(32.dp)
            )
    )

}

@Composable
private fun ForecastValue(
    modifier: Modifier = Modifier,
    degree: String,
    description: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = degree,
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.2f)
                    ),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}