package com.example.superfitness.domain.location

interface GeocoderHelper {
    suspend fun getAddressFromLocation(
        latitude: Double,
        longitude: Double
    ): String?
}