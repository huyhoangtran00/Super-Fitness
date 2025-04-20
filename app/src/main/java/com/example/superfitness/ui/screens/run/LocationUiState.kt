package com.example.superfitness.ui.screens.run

import com.google.android.gms.maps.model.LatLng

data class LocationUiState(
    val currentLocation: LatLng = LatLng(21.028511, 105.804817), //Hanoi
    val pathPoints: List<LatLng> = emptyList(),
    val distanceInMeters: Int = 0,
    val durationTimerInMillis: Long = 0L,
    val speedInKmH: Float = 0f,
    val bearing: Float = 0f,
    val steps: Int = 0,
    val isTracking: Boolean = false,
    var isFirstRun: Boolean = true,
)