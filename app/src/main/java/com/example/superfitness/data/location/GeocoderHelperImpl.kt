package com.example.superfitness.data.location

import android.os.Build
import com.example.superfitness.domain.location.GeocoderHelper
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class GeocoderHelperImpl(private val geocoderWrapper: GeocoderWrapper) : GeocoderHelper {
    override suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    withTimeoutOrNull(3000L) {
                        suspendCoroutine { continuation ->
                            geocoderWrapper.getFromLocation(latitude, longitude, 1) { addresses ->
                                val addressLine =
                                        removeCountryString(
                                                addresses.firstOrNull()?.getAddressLine(0)
                                        )
                                continuation.resume(addressLine)
                            }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoderWrapper.getFromLocation(latitude, longitude, 1)
                    removeCountryString(addresses?.firstOrNull()?.getAddressLine(0))
                }
            } catch (e: Exception) {
                // If lat or long is invalid
                null
            }
        }
    }

    private fun removeCountryString(address: String?): String? {
        if (address == null) {
            return null
        }
        val stringsList = address.split(',')
        return if (stringsList.size > 1) {
            stringsList.dropLast(1).joinToString()
        } else {
            address
        }
    }
}
