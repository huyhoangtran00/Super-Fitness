package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.location.LocationManager
import com.example.superfitness.ui.screens.run.TrackingService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class RunViewModel(
    private val locationManager: LocationManager
): ViewModel() {
    val isGpsAvailable = locationManager
        .isGpsAvailable
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000L),
            initialValue = false
        )

    var locationUiState = TrackingService.locationUiState
}