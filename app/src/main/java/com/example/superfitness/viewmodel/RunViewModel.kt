package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.data.local.db.dao.RunDao
import com.example.superfitness.data.local.db.entity.RunEntity
import com.example.superfitness.location.LocationManager
import com.example.superfitness.repository.RunRepository
import com.example.superfitness.ui.screens.run.TrackingScreen
import com.example.superfitness.ui.screens.run.TrackingService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RunViewModel(
    private val locationManager: LocationManager,
    private val runRepository: RunRepository
): ViewModel() {
    val isGpsAvailable = locationManager
        .isGpsAvailable
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000L),
            initialValue = false
        )

    var locationUiState = TrackingService.locationUiState

    /**
     * Add new run to the database
     */
    fun addRun(runEntity: RunEntity) {
        viewModelScope.launch {
            runRepository.addRun(runEntity)
        }
    }
}