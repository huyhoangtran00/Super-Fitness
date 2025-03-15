package com.example.superfitness.data.mapper

import android.annotation.SuppressLint
import android.util.Log
import com.example.superfitness.data.remote.api.ForecastWeatherDataDto
import com.example.superfitness.data.remote.api.ForecastWeatherDto
import com.example.superfitness.data.remote.api.WeatherDataDto
import com.example.superfitness.data.remote.api.WeatherDto
import com.example.superfitness.domain.weather.ForecastWeatherData
import com.example.superfitness.domain.weather.ForecastWeatherInfo
import com.example.superfitness.domain.weather.WeatherData
import com.example.superfitness.domain.weather.WeatherInfo
import com.example.superfitness.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

private data class IndexedForecastWeatherData(
    val time : String,
    val data: ForecastWeatherData
)

@SuppressLint("NewApi")
fun WeatherDataDto.toWeatherDataMap(): Pair<List<IndexedWeatherData>,Map<Int, List<WeatherData>>> {
    val oneStep = time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]
        val visible = visibility[index]
        val precipitation = precipitationList[index]
        IndexedWeatherData(
            index = index,
            data = WeatherData(
                time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                temperatureCelsius = temperature,
                pressure = pressure,
                windSpeed = windSpeed,
                humidity = humidity,
                weatherType = WeatherType.fromWMO(weatherCode),
                visibility = visible,
                precipitation = precipitation
            )
        )
    }

    return Pair(oneStep.toList(), oneStep.groupBy {
        it.index / 24
    }.mapValues {
        it.value.map { it.data }
    })
}

@SuppressLint("NewApi")
fun ForecastWeatherDataDto.toForecastWeatherDataMap():  List<Pair<String,ForecastWeatherData>> {
    return time.mapIndexed { index, time ->
        val minTemperature = minTemperatures[index]
        val maxTemperature = maxTemperatures[index]
        val rainItem      = rainList[index]
        val weatherCode = weatherCodes[index]
//        val vis = visibility[index]
        val uvIndex  = uvIndexList[index]

        IndexedForecastWeatherData(
            time = time,
            data = ForecastWeatherData(
                minTemperature = minTemperature.toFloat(),
                maxTemperature = maxTemperature.toFloat(),
                precipitation_probability= rainItem,
                weatherType = WeatherType.fromWMO(weatherCode),
                visibility = 1,
                uvIndex =  ceil(uvIndex.toDouble()).toInt()

            )
        )
    }.map { data -> Pair(data.time, data.data) }
}

@SuppressLint("NewApi")
fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val (dataWeatherList , weatherDataMap) = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if(now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        allWeatherDataList = dataWeatherList ,
        currentWeatherData = currentWeatherData
    )
}

@SuppressLint("NewApi")
fun ForecastWeatherDto.toForecastWeatherInfo(): ForecastWeatherInfo {
    val weatherDataMap = weatherData.toForecastWeatherDataMap()

    return ForecastWeatherInfo(
        weatherDataPerDay = weatherDataMap,
    )
}