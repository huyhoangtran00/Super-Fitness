package com.example.superfitness.repository

import android.location.Address
import android.location.Location

interface ILocationTracker {
    suspend fun getCurrentLocation(): Pair<Address?, Location?>?
}