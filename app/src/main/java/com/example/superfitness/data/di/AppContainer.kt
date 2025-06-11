package com.example.superfitness.data.di

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import com.example.superfitness.data.connectivity.ConnectivityObserverImpl
import com.example.superfitness.data.local.AppDatabase
import com.example.superfitness.data.location.GeocoderHelperImpl
import com.example.superfitness.data.mapper_impl.ApiDailyMapper
import com.example.superfitness.data.mapper_impl.ApiHourlyMapper
import com.example.superfitness.data.mapper_impl.ApiWeatherMapper
import com.example.superfitness.data.mapper_impl.ApiCurrentWeatherMapper
import com.example.superfitness.data.remote.WeatherApi
import com.example.superfitness.data.location.LocationManagerImpl
import com.example.superfitness.data.repository.OfflineRunRepository
import com.example.superfitness.data.repository.WeatherRepositoryImpl
import com.example.superfitness.domain.connectivity.ConnectivityObserver
import com.example.superfitness.domain.location.GeocoderHelper
import com.example.superfitness.domain.location.LocationManager
import com.example.superfitness.domain.repository.RunRepository
import com.example.superfitness.domain.repository.WeatherRepository
import com.example.superfitness.domain.usecases.get_weather.GetWeatherUseCase
import com.example.superfitness.utils.K
import com.google.android.gms.location.LocationServices
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val connectivityObserver: ConnectivityObserver
    val locationManager: LocationManager
    val geocoderHelper: GeocoderHelper
    val sensorManager: SensorManager
    val runRepository: RunRepository
    val weatherRepository: WeatherRepository
    val getUserUseCase: GetWeatherUseCase
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
        ConnectivityObserverImpl(context)
    }

    override val locationManager: LocationManager by lazy {
        LocationManagerImpl(
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
        WeatherRepositoryImpl(
            weatherApi = retrofitServiceWeatherApi
        )
    }

    override val getUserUseCase: GetWeatherUseCase =
        GetWeatherUseCase(
            repository = weatherRepository,
            apiWeatherMapper =  ApiWeatherMapper(
                apiDailyWeatherMapper = ApiDailyMapper(),
                apiHourlyWeatherMapper = ApiHourlyMapper(),
                apiCurrentWeatherMapper = ApiCurrentWeatherMapper()
            )
        )

    override val geocoderHelper: GeocoderHelper by lazy {
        GeocoderHelperImpl(context)
    }
}