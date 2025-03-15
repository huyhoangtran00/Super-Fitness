package com.example.superfitness.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.superfitness.R
import com.example.superfitness.common.Utils
import com.example.superfitness.data.mapper.IndexedWeatherData
import com.example.superfitness.viewmodel.ForecastWeatherState
import com.example.superfitness.viewmodel.WeatherState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@SuppressLint("NewApi", "SuspiciousIndentation")
@Composable
fun WeatherCard(
    state: WeatherState,
    forecastState: ForecastWeatherState,
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
                .paint(
                    painterResource(R.drawable.bg_cool),
                    contentScale = ContentScale.FillBounds
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(60.dp))
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0x44000000), Color(0x55000000)),
                                    startY = 0f,
                                    endY = 500f
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.wrapContentSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = formattedDate,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = time,
                                            color = Color.White,
                                            fontSize = 20.sp
                                        )
                                    }

                                    AsyncImage(
                                        model = "https://example.com/cloud-icon.png",
                                        contentDescription = "Cloud Icon",
                                        modifier = Modifier.size(40.dp)
                                    )
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
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${data.temperatureCelsius}°C",
                                    color = Color.White,
                                    fontSize = 48.sp
                                )

                                Text(text = state.address?.getAddressLine(0)?.split(",")?.get(2) ?:
                                "${state.address?.adminArea}", color = Color(0xFF81D4FA))
                                Text(text = activities, color = Color(0xFFFFAB91), style = TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp,
                                ), textAlign = TextAlign.Center)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    WeatherDetailRow(state, forecastWeatherList[0].second.uvIndex)
                    Spacer(modifier = Modifier.height(20.dp))
                    WeatherHourlyForecast(state.weatherInfo.allWeatherDataList)
                    Spacer(modifier = Modifier.height(30.dp))
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x44000000), Color(0x55000000)),
                    startY = 0f,
                    endY = 500f
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Row(modifier = Modifier.wrapContentSize()) {
                Image(painter = painterResource(R.drawable.rainy), contentDescription = "", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Khả năng mưa", color = Color.White)
            }
            Spacer(modifier = Modifier.width(30.dp))
            Row(modifier = Modifier.wrapContentSize()) {
                Image(painter = painterResource(R.drawable.snowflake), contentDescription = "", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Khả năng tuyết", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.width(40.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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

@Composable
fun HourlyWeatherItem(time: String, temperature: String, rainChance: String, icon: Int) {
    Column(
        modifier = Modifier.width(80.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = time, fontSize = 14.sp, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = temperature,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
                ){
                Image(
                    painter = painterResource(id = R.drawable.rainy),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = rainChance, fontSize = 12.sp, color = Color.Cyan)
            }
        }
    }
}

@Composable
fun WeatherDetailRow(currentWeatherState: WeatherState, uvIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherDetailItem(
            icon = R.drawable.humidity,
            value = "${currentWeatherState.weatherInfo?.currentWeatherData?.humidity}%",
            description = "Độ ẩm",
            endPadding = 4
        )
        WeatherDetailItem(
            icon = R.drawable.rays,
            value = "$uvIndex",
            description = "Chỉ số UV",
            startPadding = 4,
            endPadding = 4
        )
        WeatherDetailItem(
            icon = R.drawable.ic_eye,
            value = "${((currentWeatherState.weatherInfo?.currentWeatherData?.visibility ?: 0) / 1000).toInt()} km",
            description = "Tầm nhìn",
            startPadding = 4,
            endPadding = 4
        )
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = startPadding.dp, end = endPadding.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x66000000), Color(0x66000000)),
                    startY = 0f,
                    endY = 500f
                ), shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 8.dp, bottom = 8.dp)
            .width(115.dp)

    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = description,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        Text(text = description, fontSize = 14.sp, color = Color.White)
    }
}

@Composable
fun WeatherItem(index: Int, day: String, temperature: String, rainChance: String, icon: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x44000000), Color(0x55000000)),
                        startY = 0f,
                        endY = 500f
                    ),
                    shape = if (index == 0) RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    ) else RoundedCornerShape(0.dp)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(fraction = 0.2f),
                text = day,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
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
                    Text(text = rainChance, fontSize = 14.sp, color = Color.White)

                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(fraction = 0.55f),
                text = temperature,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun PreviewWeatherCard() {
    Column(modifier = Modifier.fillMaxSize()) {
//        WeatherCard()
    }
}