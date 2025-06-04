package com.example.superfitness.data

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import com.example.superfitness.connectivity.AndroidConnectivityObserver
import com.example.superfitness.connectivity.ConnectivityObserver
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.mapper_impl.ApiDailyMapper
import com.example.superfitness.data.mapper_impl.ApiHourlyMapper
import com.example.superfitness.data.mapper_impl.ApiWeatherMapper
import com.example.superfitness.data.mapper_impl.CurrentWeatherMapper
import com.example.superfitness.data.remote.WeatherApi
import com.example.superfitness.location.AndroidLocationManager
import com.example.superfitness.location.LocationManager
import com.example.superfitness.data.repository.OfflineRunRepository
import com.example.superfitness.data.repository.WeatherRepositoryImpl
import com.example.superfitness.domain.repository.RunRepository
import com.example.superfitness.domain.repository.WeatherRepository
import com.example.superfitness.utils.K
import com.google.android.gms.location.LocationServices
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


interface AppContainer {
    val connectivityObserver: ConnectivityObserver
    val locationManager: LocationManager
    val runRepository: RunRepository
    val sensorManager: SensorManager
    val weatherRepository: WeatherRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    // Base Url + Api Service
    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(K.API_BASE_URL)
        .build()

    private val retrofitServiceWeatherApi: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }

    override val connectivityObserver: ConnectivityObserver by lazy {
        AndroidConnectivityObserver(context)
    }

    override val locationManager: LocationManager by lazy {
        AndroidLocationManager(
            context,
            LocationServices.getFusedLocationProviderClient(context)
        )
    }
    override val sensorManager: SensorManager by lazy {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override val runRepository: RunRepository by lazy {
        val database = AppDatabase.getDatabase(context)
        OfflineRunRepository(
            database.runDao()
        )
    }

    override val weatherRepository: WeatherRepository by lazy {
        val apiWeatherMapper = ApiWeatherMapper(
            apiDailyMapper = ApiDailyMapper(),
            apiHourlyMapper = ApiHourlyMapper(),
            apiCurrentWeatherMapper = CurrentWeatherMapper()
        )

        WeatherRepositoryImpl(
            weatherApi = retrofitServiceWeatherApi,
            apiWeatherMapper = apiWeatherMapper
        )
    }
}