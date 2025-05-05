package com.example.superfitness.data.local.converter

import android.util.Log
import com.example.superfitness.domain.weather.ForecastWeatherData
import com.example.superfitness.domain.weather.WeatherType
import com.google.gson.*
import java.lang.reflect.Type

class ForecastWeatherDataAdapter : JsonSerializer<List<Pair<String, ForecastWeatherData>>>, JsonDeserializer<List<Pair<String, ForecastWeatherData>>> {
    private val TAG = "ForecastWeatherDataAdapter"

    override fun serialize(
        src: List<Pair<String, ForecastWeatherData>>?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val jsonArray = JsonArray()
        src?.forEach { pair ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("first", pair.first)
            
            val secondObject = JsonObject()
            secondObject.addProperty("minTemperature", pair.second.minTemperature)
            secondObject.addProperty("maxTemperature", pair.second.maxTemperature)
            secondObject.addProperty("precipitation_probability", pair.second.precipitation_probability)
            secondObject.addProperty("visibility", pair.second.visibility)
            secondObject.addProperty("uvIndex", pair.second.uvIndex)
            
            val weatherTypeObject = JsonObject()
            weatherTypeObject.addProperty("code", pair.second.weatherType.code)
            weatherTypeObject.addProperty("iconRes", pair.second.weatherType.iconRes)
            weatherTypeObject.addProperty("weatherDesc", pair.second.weatherType.weatherDesc)
            
            secondObject.add("weatherType", weatherTypeObject)
            jsonObject.add("second", secondObject)
            jsonArray.add(jsonObject)
        }
        return jsonArray
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Pair<String, ForecastWeatherData>> {
        val result = mutableListOf<Pair<String, ForecastWeatherData>>()
        
        try {
            if (json is JsonArray) {
                json.forEach { element ->
                    if (element is JsonObject) {
                        val firstElement = element.get("first")
                        val secondElement = element.get("second")
                        
                        if (firstElement != null && secondElement is JsonObject) {
                            val date = firstElement.asString
                            
                            // Safely get values with defaults
                            val minTemperature = secondElement.get("minTemperature")?.asFloat ?: 0f
                            val maxTemperature = secondElement.get("maxTemperature")?.asFloat ?: 0f
                            val precipitationProbability = secondElement.get("precipitation_probability")?.asInt ?: 0
                            val visibility = secondElement.get("visibility")?.asLong ?: 1L
                            val uvIndex = secondElement.get("uvIndex")?.asInt ?: 0
                            
                            val weatherTypeElement = secondElement.get("weatherType")
                            val weatherType = if (weatherTypeElement is JsonObject) {
                                try {
                                    val code = weatherTypeElement.get("code")?.asInt ?: 0
                                    WeatherType.fromWMO(code)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error creating WeatherType from code: ${weatherTypeElement.get("code")?.asInt}", e)
                                    WeatherType.ClearSky // Default to ClearSky if there's an error
                                }
                            } else {
                                WeatherType.ClearSky // Default to ClearSky if weatherType is not an object
                            }
                            
                            val forecastWeatherData = ForecastWeatherData(
                                minTemperature = minTemperature,
                                maxTemperature = maxTemperature,
                                precipitation_probability = precipitationProbability,
                                weatherType = weatherType,
                                visibility = visibility,
                                uvIndex = uvIndex
                            )
                            
                            result.add(Pair(date, forecastWeatherData))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deserializing forecast weather data", e)
        }
        
        return result
    }
} 