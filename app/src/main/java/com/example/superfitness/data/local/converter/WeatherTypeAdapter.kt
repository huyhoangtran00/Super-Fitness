package com.example.superfitness.data.local.converter

import com.example.superfitness.domain.weather.WeatherType
import com.google.gson.*
import java.lang.reflect.Type

class WeatherTypeAdapter : JsonSerializer<WeatherType>, JsonDeserializer<WeatherType> {
    override fun serialize(
        src: WeatherType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.code ?: 0)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WeatherType {
        return when (json) {
            is JsonPrimitive -> {
                val code = json.asInt
                WeatherType.fromWMO(code)
            }
            is JsonObject -> {
                val code = json.get("code")?.asInt ?: 0
                WeatherType.fromWMO(code)
            }
            else -> WeatherType.ClearSky
        }
    }
} 