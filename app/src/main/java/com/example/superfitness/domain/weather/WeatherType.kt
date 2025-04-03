package com.example.superfitness.domain.weather

import androidx.annotation.DrawableRes
import com.example.superfitness.R

sealed class WeatherType(
    val weatherDesc: String,
    @DrawableRes val iconRes: Int,
    val code: Int
) {
    data object ClearSky : WeatherType(
        weatherDesc = "Clear sky",
        iconRes = R.drawable.ic_sunny,
        code = 0
    )
    data object MainlyClear : WeatherType(
        weatherDesc = "Mainly clear",
        iconRes = R.drawable.ic_cloudy,
        code = 1
    )
    data object PartlyCloudy : WeatherType(
        weatherDesc = "Partly cloudy",
        iconRes = R.drawable.ic_cloudy,
        code = 2
    )
    data object Overcast : WeatherType(
        weatherDesc = "Overcast",
        iconRes = R.drawable.ic_cloudy,
        code = 3
    )
    data object Foggy : WeatherType(
        weatherDesc = "Foggy",
        iconRes = R.drawable.ic_very_cloudy,
        code = 45
    )
    data object DepositingRimeFog : WeatherType(
        weatherDesc = "Depositing rime fog",
        iconRes = R.drawable.ic_very_cloudy,
        code = 48
    )
    data object LightDrizzle : WeatherType(
        weatherDesc = "Light drizzle",
        iconRes = R.drawable.ic_rainshower,
        code = 51
    )
    data object ModerateDrizzle : WeatherType(
        weatherDesc = "Moderate drizzle",
        iconRes = R.drawable.ic_rainshower,
        code = 53
    )
    data object DenseDrizzle : WeatherType(
        weatherDesc = "Dense drizzle",
        iconRes = R.drawable.ic_rainshower,
        code = 55
    )
    data object LightFreezingDrizzle : WeatherType(
        weatherDesc = "Slight freezing drizzle",
        iconRes = R.drawable.ic_snowyrainy,
        code = 56
    )
    data object DenseFreezingDrizzle : WeatherType(
        weatherDesc = "Dense freezing drizzle",
        iconRes = R.drawable.ic_snowyrainy,
        code = 57
    )
    data object SlightRain : WeatherType(
        weatherDesc = "Slight rain",
        iconRes = R.drawable.ic_rainy,
        code = 61
    )
    data object ModerateRain : WeatherType(
        weatherDesc = "Rainy",
        iconRes = R.drawable.ic_rainy,
        code = 63
    )
    data object HeavyRain : WeatherType(
        weatherDesc = "Heavy rain",
        iconRes = R.drawable.ic_rainy,
        code = 65
    )
    data object HeavyFreezingRain : WeatherType(
        weatherDesc = "Heavy freezing rain",
        iconRes = R.drawable.ic_snowyrainy,
        code = 67
    )
    data object SlightSnowFall : WeatherType(
        weatherDesc = "Slight snow fall",
        iconRes = R.drawable.ic_snowy,
        code = 71
    )
    data object ModerateSnowFall : WeatherType(
        weatherDesc = "Moderate snow fall",
        iconRes = R.drawable.ic_heavysnow,
        code = 73
    )
    data object HeavySnowFall : WeatherType(
        weatherDesc = "Heavy snow fall",
        iconRes = R.drawable.ic_heavysnow,
        code = 75
    )
    data object SnowGrains : WeatherType(
        weatherDesc = "Snow grains",
        iconRes = R.drawable.ic_heavysnow,
        code = 77
    )
    data object SlightRainShowers : WeatherType(
        weatherDesc = "Slight rain showers",
        iconRes = R.drawable.ic_rainshower,
        code = 80
    )
    data object ModerateRainShowers : WeatherType(
        weatherDesc = "Moderate rain showers",
        iconRes = R.drawable.ic_rainshower,
        code = 81
    )
    data object ViolentRainShowers : WeatherType(
        weatherDesc = "Violent rain showers",
        iconRes = R.drawable.ic_rainshower,
        code = 82
    )
    data object SlightSnowShowers : WeatherType(
        weatherDesc = "Light snow showers",
        iconRes = R.drawable.ic_snowy,
        code = 85
    )
    data object HeavySnowShowers : WeatherType(
        weatherDesc = "Heavy snow showers",
        iconRes = R.drawable.ic_snowy,
        code = 86
    )
    data object ModerateThunderstorm : WeatherType(
        weatherDesc = "Moderate thunderstorm",
        iconRes = R.drawable.ic_thunder,
        code = 95
    )
    data object SlightHailThunderstorm : WeatherType(
        weatherDesc = "Thunderstorm with slight hail",
        iconRes = R.drawable.ic_rainythunder,
        code = 96
    )
    data object HeavyHailThunderstorm : WeatherType(
        weatherDesc = "Thunderstorm with heavy hail",
        iconRes = R.drawable.ic_rainythunder,
        code = 99
    )

    companion object {
        fun fromWMO(code: Int): WeatherType {
            return when (code) {
                ClearSky.code -> ClearSky
                MainlyClear.code -> MainlyClear
                PartlyCloudy.code -> PartlyCloudy
                Overcast.code -> Overcast
                Foggy.code -> Foggy
                DepositingRimeFog.code -> DepositingRimeFog
                LightDrizzle.code -> LightDrizzle
                ModerateDrizzle.code -> ModerateDrizzle
                DenseDrizzle.code -> DenseDrizzle
                LightFreezingDrizzle.code -> LightFreezingDrizzle
                DenseFreezingDrizzle.code -> DenseFreezingDrizzle
                SlightRain.code -> SlightRain
                ModerateRain.code -> ModerateRain
                HeavyRain.code -> HeavyRain
                HeavyFreezingRain.code -> HeavyFreezingRain
                SlightSnowFall.code -> SlightSnowFall
                ModerateSnowFall.code -> ModerateSnowFall
                HeavySnowFall.code -> HeavySnowFall
                SnowGrains.code -> SnowGrains
                SlightRainShowers.code -> SlightRainShowers
                ModerateRainShowers.code -> ModerateRainShowers
                ViolentRainShowers.code -> ViolentRainShowers
                SlightSnowShowers.code -> SlightSnowShowers
                HeavySnowShowers.code -> HeavySnowShowers
                ModerateThunderstorm.code -> ModerateThunderstorm
                SlightHailThunderstorm.code -> SlightHailThunderstorm
                HeavyHailThunderstorm.code -> HeavyHailThunderstorm
                else -> throw IllegalArgumentException("Invalid WMO code: $code")
            }
        }
    }
}
