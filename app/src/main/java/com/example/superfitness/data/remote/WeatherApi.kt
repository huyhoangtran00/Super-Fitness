package com.example.superfitness.data.remote

import com.example.superfitness.data.remote.models.ApiWeather
import com.example.superfitness.utils.ApiParameters
import com.example.superfitness.utils.K
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET(K.END_POINT)
    suspend fun getWeatherData(
        @Query(ApiParameters.LATITUDE) latitude:Double,
        @Query(ApiParameters.LONGITUDE) longitude:Double,
        @Query(ApiParameters.DAILY) daily:Array<String> = arrayOf(
            "weather_code",
            "rain_sum",
            "temperature_2m_max",
            "temperature_2m_min",
            "wind_speed_10m_max",
            "wind_direction_10m_dominant",
            "sunrise",
            "sunset",
            "uv_index_max",
            "precipitation_probability_max"
        ),
        @Query(ApiParameters.CURRENT_WEATHER) currentWeather: Array<String> = arrayOf(
            "temperature_2m",
            "is_day",
            "weather_code",
            "wind_speed_10m",
            "wind_direction_10m",
            "relative_humidity_2m",
            "apparent_temperature",
        ),
        @Query(ApiParameters.HOURLY) hourlyWeather: Array<String> = arrayOf(
            "weather_code",
            "temperature_2m",
            "relative_humidity_2m",
            "uv_index",
            "rain",
            "precipitation_probability"
        ),
        @Query(ApiParameters.TIME_FORMAT) timeformat: String = "unixtime",
        @Query(ApiParameters.TIME_ZONE) timeZone: String = "Asia/Bangkok",

        ):ApiWeather
}