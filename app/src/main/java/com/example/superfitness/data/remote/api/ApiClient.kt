package com.example.superfitness.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast?hourly=temperature_2m,weathercode,relativehumidity_2m,windspeed_10m,pressure_msl,visibility,precipitation_probability&timezone=Asia%2FBangkok")
    suspend fun getWeatherData(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double
    ): WeatherDto

    @GET("v1/forecast?daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max,weather_code,uv_index_max")
    suspend fun getForecastWeatherData(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double
    ): ForecastWeatherDto
}