package com.example.superfitness.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.superfitness.R
import com.example.superfitness.domain.weather.AirQualityData
import com.example.superfitness.domain.weather.AirQualityInfo

@Composable
fun AirQualityCard(airQualityState: AirQualityInfo) {
    if (airQualityState.currentAirQuality != null) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(120.dp)
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(Color(0x44000000), Color(0x55000000)),
//                        startY = 0f,
//                        endY = 500f
//                    ),
//                    shape = RoundedCornerShape(16.dp)
//                )
//                .padding(8.dp)
//        ) {
            LazyRow (
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item{
                    AirQualityItem(
                        icon = R.drawable.operation_pm10,
                        value = "${airQualityState.currentAirQuality.pm10.toInt()}",
                        description = "PM10 (μg/m³)"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    AirQualityItem(
                        icon = R.drawable.pm25,
                        value = "${airQualityState.currentAirQuality.pm2_5.toInt()}",
                        description = "PM2.5 (μg/m³)"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
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
    icon: Int,
    value: String,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x66000000), Color(0x66000000)),
                    startY = 0f,
                    endY = 500f
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .width(90.dp)
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = icon),
            contentDescription = description,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
        )
        Text(
            text = description,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}