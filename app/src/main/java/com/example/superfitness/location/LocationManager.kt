package com.example.superfitness.location

import android.content.Context
import android.os.Looper
import com.example.superfitness.utils.FATEST_LOCATION_INTERVAL
import com.example.superfitness.utils.LOCATION_UPDATE_INTERVAL
import com.example.superfitness.utils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface LocationManager {
    val isGpsAvailable: Flow<Boolean>
}

class AndroidLocationManager(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
): LocationManager {

    override val isGpsAvailable: Flow<Boolean>
        get() = callbackFlow {

            if (!context.hasLocationPermission()) {
                trySend(false)
                close()
                return@callbackFlow
            }

            val callback = object : LocationCallback() {
                override fun onLocationAvailability(p0: LocationAvailability) {
                    super.onLocationAvailability(p0)
                    trySend(p0.isLocationAvailable)
                }
            }

            val locationRequest = LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FATEST_LOCATION_INTERVAL)
                .setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL)
                .build()

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                trySend(false) // Handle permission issues
            }

            // Stop updates when Flow is closed
            awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
        }


}