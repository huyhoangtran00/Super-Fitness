package com.example.superfitness.data.remote.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiWeather(
    @SerialName("current")
    val apiCurrentWeather: ApiCurrentWeather,
    @SerialName("current_units")
    val currentUnits: CurrentUnits,
    @SerialName("daily")
    val apiDailyWeather: ApiDailyWeather,
    @SerialName("daily_units")
    val dailyUnits: DailyUnits,
    @SerialName("elevation")
    val elevation: Int,
    @SerialName("generationtime_ms")
    val generationtimeMs: Double,
    @SerialName("hourly")
    val apiHourlyWeather: ApiHourlyWeather,
    @SerialName("hourly_units")
    val hourlyUnits: HourlyUnits,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    @SerialName("utc_offset_seconds")
    val utcOffsetSeconds: Int
)