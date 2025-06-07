package com.example.superfitness.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil3.compose.AsyncImage
import com.example.superfitness.R
import com.example.superfitness.common.Utils
import com.example.superfitness.data.mapper.IndexedWeatherData
import com.example.superfitness.domain.weather.AirQualityInfo
import com.example.superfitness.utils.BLUE
import com.example.superfitness.utils.GREEN
import com.example.superfitness.utils.RED
import com.example.superfitness.viewmodel.ForecastWeatherState
import com.example.superfitness.viewmodel.WeatherState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

private val TAG = "WeatherCard"
@SuppressLint("NewApi", "SuspiciousIndentation")
@Composable
fun WeatherCard(
    state: WeatherState,
    forecastState: ForecastWeatherState,
    airQualityState: AirQualityInfo
) {

    var time by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        while (scope.isActive) {
            val date = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
            val formattedDate = dateFormat.format(date)
            time = formattedDate
            delay(1000)
        }
    }
    if (state.weatherInfo?.currentWeatherData != null && forecastState.weatherInfoList != null) {
        val data = state.weatherInfo.currentWeatherData
        val forecastWeatherList = forecastState.weatherInfoList
        val resultDataMap = forecastWeatherList.map { Pair(Utils.getDayOfWeekFromDate(it.first), it.second) }
        val activities = Utils.suggestActivityByCode(data.weatherType.code)
        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("E, 'thg' M d", Locale("vi", "VN"))
        val formattedDate = dateFormat.format(date)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            item {
                Spacer(Modifier.height(40.dp))
                Column(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(BLUE.toColorInt())),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.wrapContentSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column {
                                        Text(
                                            text = formattedDate,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                        )
                                        Text(
                                            text = time,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                        )
                                    }

                                }
                                Row(
                                    modifier = Modifier.wrapContentSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier.wrapContentSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = data.weatherType.iconRes), // Replace with your weather icon
                                            contentDescription = "Weather Icon",
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = data.weatherType.weatherDesc,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${data.temperatureCelsius}°C",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )

                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = state.address?.getAddressLine(0)?.split(",")?.get(0) + "," +
                                        state.address?.getAddressLine(0)?.split(",")?.get(1),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = activities,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    ),
                                    textAlign = TextAlign.Center
                                )


                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Timber.tag(TAG).d("WeatherCard: ====> ${forecastWeatherList.getOrNull(0)} ")
                    Log.d(TAG, "WeatherCard: ====> ${forecastWeatherList.getOrNull(0)?.first} ")
                    Log.d(TAG, "WeatherCard: ====> ${forecastWeatherList.getOrNull(0)?.second} ")
                    WeatherDetailRow(state, forecastWeatherList.getOrNull(0)?.second?.uvIndex ?: 0)
                    Spacer(modifier = Modifier.height(16.dp))
                    AirQualityCard(airQualityState)
                    Spacer(modifier = Modifier.height(16.dp))
                    WeatherHourlyForecast(state.weatherInfo.allWeatherDataList)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            items(resultDataMap.size) { index ->
                WeatherItem(
                    index = index,
                    day = resultDataMap[index].first,
                    temperature = "${resultDataMap[index].second.minTemperature}° - ${resultDataMap[index].second.maxTemperature}°",
                    rainChance = "${resultDataMap[index].second.precipitation_probability}%",
                    icon = resultDataMap[index].second.weatherType.iconRes
                )
            }
        }
    }

}

@SuppressLint("NewApi")
@Composable
fun WeatherHourlyForecast(dataResult: List<IndexedWeatherData>) {

    val index = dataResult.indexOfFirst { it.data.time >= LocalDateTime.now() }
    val dataWeatherList = dataResult.subList(index, if (index + 24 > dataResult.size) dataResult.size else index + 24)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(BLUE.toColorInt())),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(R.drawable.rainy), contentDescription = "", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Khả năng mưa",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dataWeatherList.size) { index ->
                    HourlyWeatherItem(
                        time = dataWeatherList[index].data.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        temperature = "${dataWeatherList[index].data.temperatureCelsius}°",
                        rainChance = "${dataWeatherList[index].data.precipitation}%",
                        icon = dataWeatherList[index].data.weatherType.iconRes
                    )
                }
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(time: String, temperature: String, rainChance: String, icon: Int) {
    Column(
        modifier = Modifier.width(80.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = temperature,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
                ){
                Image(
                    painter = painterResource(id = R.drawable.rainy),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = rainChance,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun WeatherDetailRow(currentWeatherState: WeatherState, uvIndex: Int) {
    LazyRow (
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        item{
            WeatherDetailItem(
                icon = R.drawable.humidity,
                value = "${currentWeatherState.weatherInfo?.currentWeatherData?.humidity}%",
                description = "Độ ẩm",
                endPadding = 4
            )
            Spacer(modifier = Modifier.width(16.dp))

            WeatherDetailItem(
                icon = R.drawable.rays,
                value = "$uvIndex",
                description = "Chỉ số UV",
                startPadding = 4,
                endPadding = 4
            )
        }

    }
}

@Composable
fun WeatherDetailItem(
    startPadding: Int = 0,
    endPadding: Int = 0,
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

@Composable
fun WeatherItem(index: Int, day: String, temperature: String, rainChance: String, icon: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(32.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = if (index == 0) RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    ) else RoundedCornerShape(0.dp)
                )
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(fraction = 0.2f),
                text = day,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Column(
                modifier = Modifier.fillMaxWidth(fraction = 0.3f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = icon), // Replace with your weather icon
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(48.dp)
                )
                // Rain chance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rainy),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = rainChance, fontSize = 14.sp, color = Color.Black)

                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(fraction = 0.55f),
                text = temperature,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.9f),
            thickness = 1.dp,
            color = Color(BLUE.toColorInt())
        )
    }
}

@Preview
@Composable
fun PreviewWeatherCard() {
    Column(modifier = Modifier.fillMaxSize()) {
//        WeatherCard()
    }
}