package com.example.superfitness.data

import android.app.Service
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.superfitness.connectivity.AndroidConnectivityObserver
import com.example.superfitness.connectivity.ConnectivityObserver
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.location.AndroidLocationManager
import com.example.superfitness.location.LocationManager
import com.example.superfitness.repository.OfflineRunRepository
import com.example.superfitness.repository.RunRepository
import com.example.superfitness.ui.screens.run.TrackingService
import com.google.android.gms.location.LocationServices


interface AppContainer {
    val connectivityObserver: ConnectivityObserver
    val locationManager: LocationManager
    val runRepository: RunRepository
    val sensorManager: SensorManager
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    override val connectivityObserver: ConnectivityObserver by lazy {
        AndroidConnectivityObserver(context)
    }

    override val locationManager: LocationManager by lazy {
        AndroidLocationManager(
            context,
            LocationServices.getFusedLocationProviderClient(context)
        )
    }

    override val runRepository: RunRepository by lazy {
        val database = AppDatabase.getDatabase(context)
        OfflineRunRepository(
            database.runDao()
        )
    }
    override val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

}