package com.example.superfitness.data

import android.content.Context
import com.example.superfitness.connectivity.AndroidConnectivityObserver
import com.example.superfitness.connectivity.ConnectivityObserver
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.location.AndroidLocationManager
import com.example.superfitness.location.LocationManager
import com.example.superfitness.repository.OfflineRunRepository
import com.example.superfitness.repository.RunRepository
import com.google.android.gms.location.LocationServices


interface AppContainer {
    val connectivityObserver: ConnectivityObserver
    val locationManager: LocationManager
    val runRepository: RunRepository
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

}