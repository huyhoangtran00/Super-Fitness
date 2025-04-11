package com.example.superfitness.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.ui.tracking.TrackingService
import kotlinx.coroutines.launch

class ShareViewModel: ViewModel() {

    var locationUiState = TrackingService.locationUiState

    init {
        viewModelScope.launch {

        }
    }
}