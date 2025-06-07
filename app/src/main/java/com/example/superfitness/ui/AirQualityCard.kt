package com.example.superfitness.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.superfitness.R
import com.example.superfitness.domain.weather.AirQualityData
import com.example.superfitness.domain.weather.AirQualityInfo
import com.example.superfitness.utils.BLUE
import com.example.superfitness.utils.GREEN

@Composable
fun AirQualityCard(airQualityState: AirQualityInfo) {
    if (airQualityState.currentAirQuality != null) {
            LazyRow (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item{
                    AirQualityItem(
                        icon = R.drawable.pm25,
                        value = "${airQualityState.currentAirQuality.pm2_5.toInt()}",
                        description = "PM2.5 (μg/m³)"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AirQualityItem(
                        icon = R.drawable.co2,
                        value = "${airQualityState.currentAirQuality.carbonDioxide.toInt()}",
                        description = "CO₂ (ppm)"
                    )
                }
//            }
        }
    }
}

@Composable
fun AirQualityItem(
    modifier: Modifier = Modifier,
    icon: Int,
    value: String,
    description: String
) {
    Card(
        modifier = Modifier
            .width(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(width = 2.dp, color = Color(BLUE.toColorInt()))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = description,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }
    }
}