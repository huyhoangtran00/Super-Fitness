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
import com.example.superfitness.domain.usecases.run.AddRun
import com.example.superfitness.domain.usecases.run.DeleteRun
import com.example.superfitness.domain.usecases.run.GetAllRuns
import com.example.superfitness.domain.usecases.run.GetRun
import com.example.superfitness.domain.usecases.run.RunUseCases
import com.example.superfitness.domain.usecases.weather.GetWeatherUseCase
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
    val getWeatherUseCase: GetWeatherUseCase
    val runUseCase: RunUseCases
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    // Base Url + Api Service
    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    /**
     * Retrofit instance that has base URI (host)
     * and converter factory to build a web Service API
      */
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(K.API_BASE_URL) // Host URL
        .build()

    // LAZY INITIALIZATION
    // DefaultAppContainer is created right after Application is onCreate()
    // Lazy is used to purposely delay, until you actually need that object,
    // to avoid unnecessary computation or use of other computing resources

    /**
     * Create an implementation of the Weather APi endpoint defined by the parameter interface
     */
    private val retrofitServiceWeatherApi: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }

    // Internet Accessibility
    override val connectivityObserver: ConnectivityObserver by lazy {
        ConnectivityObserverImpl(context)
    }

    // Location
    override val locationManager: LocationManager by lazy {
        LocationManagerImpl(
            context,
            LocationServices.getFusedLocationProviderClient(context)
        )
    }

    override val geocoderHelper: GeocoderHelper by lazy {
        GeocoderHelperImpl(context)
    }

    // Sensor
    override val sensorManager: SensorManager by lazy {
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    // Repository
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

    // Use Cases
    override val getWeatherUseCase: GetWeatherUseCase =
        GetWeatherUseCase(
            repository = weatherRepository,
            apiWeatherMapper =  ApiWeatherMapper(
                apiDailyWeatherMapper = ApiDailyMapper(),
                apiHourlyWeatherMapper = ApiHourlyMapper(),
                apiCurrentWeatherMapper = ApiCurrentWeatherMapper()
            )
        )

    override val runUseCase: RunUseCases =
        RunUseCases(
            getAllRuns = GetAllRuns(runRepository),
            getRun = GetRun(runRepository),
            deleteRun = DeleteRun(runRepository),
            addRun = AddRun(runRepository)
        )


}