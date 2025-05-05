package com.example.superfitness.data.local.converter

import androidx.room.TypeConverter
import com.example.superfitness.domain.weather.WeatherData
import com.example.superfitness.domain.weather.WeatherType
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherDataConverter {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(WeatherType::class.java, WeatherTypeAdapter())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    @TypeConverter
    fun fromWeatherData(value: WeatherData?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toWeatherData(value: String?): WeatherData? {
        return value?.let {
            val type = object : TypeToken<WeatherData>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromWeatherDataList(value: List<WeatherData>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toWeatherDataList(value: String?): List<WeatherData>? {
        return value?.let {
            val type = object : TypeToken<List<WeatherData>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromWeatherDataMap(value: Map<Int, List<WeatherData>>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toWeatherDataMap(value: String?): Map<Int, List<WeatherData>>? {
        return value?.let {
            val type = object : TypeToken<Map<Int, List<WeatherData>>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
