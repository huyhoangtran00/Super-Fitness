package com.example.superfitness.data.location

import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.superfitness.domain.location.LocationManager
import com.example.superfitness.utils.FATEST_LOCATION_INTERVAL
import com.example.superfitness.utils.LOCATION_UPDATE_INTERVAL
import com.example.superfitness.utils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationManagerImpl(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
): LocationManager {

    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
        .setMinUpdateIntervalMillis(FATEST_LOCATION_INTERVAL)
        .setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL)
        .build()
    override val isGpsAvailable: Flow<Boolean>
        get() = callbackFlow {

            if (!context.hasLocationPermission()) {
                trySend(false)
                close() // close the flow if no permission
                return@callbackFlow
            }

            val availabilityCallback = object : LocationCallback() {
                override fun onLocationAvailability(p0: LocationAvailability) {
                    super.onLocationAvailability(p0)
                    trySend(p0.isLocationAvailable)
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    availabilityCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                trySend(false) // Handle permission issues
            }

            // Stop updates when Flow is closed, avoid memory leaking
            awaitClose { fusedLocationClient.removeLocationUpdates(availabilityCallback) }
        }
    override val locationUpdates: Flow<Location>
        get() = callbackFlow {
            if (!context.hasLocationPermission()) {
                close()
                return@callbackFlow
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.lastLocation?.let { location ->
                        trySend(location)
                    }
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                // Handle permission issues
            }

            // Stop update when Flow is closed
            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
}