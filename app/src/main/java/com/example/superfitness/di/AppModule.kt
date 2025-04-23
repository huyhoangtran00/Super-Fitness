package com.example.superfitness.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.remote.api.AirQualityApi
import com.example.superfitness.data.remote.api.WeatherApi
import com.example.superfitness.data.repository.AirQualityRepository
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.data.repository.WeatherRepository
import com.example.superfitness.domain.repository.IAirQualityRepository
import com.example.superfitness.domain.repository.IWeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAirQualityApi(): AirQualityApi {
        return Retrofit.Builder()
            .baseUrl("https://air-quality-api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AirQualityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context : Context): AppDatabase {
        return  Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "super_fitness_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi, 
        db: AppDatabase,
        connectivityManager: ConnectivityManager
    ): IWeatherRepository {
        return WeatherRepository(api, db.weatherDao(), connectivityManager)
    }

    @Provides
    @Singleton
    fun provideAirQualityRepository(
        api: AirQualityApi, 
        db: AppDatabase,
        connectivityManager: ConnectivityManager
    ): IAirQualityRepository {
        return AirQualityRepository(api, db.airQualityDao(), connectivityManager)
    }

    @Provides
    @Singleton
    fun provideUserDao(app : AppDatabase): UserProfileDao {
        return app.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(dao : UserProfileDao): UserProfileRepository {
        return UserProfileRepository(dao)
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(app: Application): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}