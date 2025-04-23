package com.example.superfitness.domain.weather

import androidx.room.Ignore
import java.time.LocalDateTime

data class WeatherData(
    val time: LocalDateTime,
    val temperatureCelsius: Double,
    val pressure: Double,
    val windSpeed: Double,
    val humidity: Double,
    @Ignore
    val weatherType: WeatherType ,
    val code : Int ?= null,
    val visibility : Long,
    val precipitation : Int,
)

data class ForecastWeatherData(

    val minTemperature : Float,
    val maxTemperature : Float,
    val precipitation_probability: Int,
    val weatherType: WeatherType,
    val visibility : Long,
    val uvIndex : Int

)