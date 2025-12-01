package com.example.superfitness.data.location

import android.location.Address
import android.location.Geocoder
import android.os.Build

interface GeocoderWrapper {
    fun getFromLocation(
            latitude: Double,
            longitude: Double,
            maxResults: Int,
            listener: Geocoder.GeocodeListener
    )

    fun getFromLocation(latitude: Double, longitude: Double, maxResults: Int): List<Address>?
}

class GeocoderWrapperImpl(private val geocoder: Geocoder) : GeocoderWrapper {
    override fun getFromLocation(
            latitude: Double,
            longitude: Double,
            maxResults: Int,
            listener: Geocoder.GeocodeListener
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, maxResults, listener)
        }
    }

    override fun getFromLocation(
            latitude: Double,
            longitude: Double,
            maxResults: Int
    ): List<Address>? {
        @Suppress("DEPRECATION") return geocoder.getFromLocation(latitude, longitude, maxResults)
    }
}
