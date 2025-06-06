package com.example.superfitness.domain.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationManager {
    val isGpsAvailable: Flow<Boolean>
    val locationUpdates: Flow<Location>
}