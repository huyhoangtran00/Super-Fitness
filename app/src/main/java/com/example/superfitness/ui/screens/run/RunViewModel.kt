package com.example.superfitness.ui.screens.run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.location.LocationManager
import com.example.superfitness.domain.repository.RunRepository
import com.example.superfitness.domain.usecases.run.RunUseCases
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RunViewModel(
    private val locationManager: LocationManager,
    private val runUseCases: RunUseCases
): ViewModel() {
    val isGpsAvailable = locationManager
        .isGpsAvailable
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000L),
            initialValue = false
        )

    val currentLocation: StateFlow<LatLng?> = locationManager.locationUpdates
        .map { location -> LatLng(location.latitude, location.longitude) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000L),
            initialValue = null
        )

    var locationUiState = TrackingService.locationUiState
    /**
     * Add new run to the database
     */
    fun addRun(runEntity: RunEntity) {
        viewModelScope.launch {
            runUseCases.addRun(runEntity)
        }
    }
}