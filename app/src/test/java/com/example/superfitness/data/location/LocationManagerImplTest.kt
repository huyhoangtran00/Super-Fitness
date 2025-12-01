package com.example.superfitness.data.location

import android.content.Context
import android.location.Location
import app.cash.turbine.test
import com.example.superfitness.utils.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationManagerImplTest {

    private val context: Context = mockk(relaxed = true)
    private val fusedLocationClient: FusedLocationProviderClient = mockk(relaxed = true)
    private val permissionChecker: PermissionChecker = mockk()
    private val locationManager =
            LocationManagerImpl(context, fusedLocationClient, permissionChecker)

    @Before
    fun setup() {
        mockkStatic(android.os.Looper::class)
        every { android.os.Looper.getMainLooper() } returns mockk(relaxed = true)
    }

    @Test
    fun `isGpsAvailable emits false when no permission`() = runTest {
        every { permissionChecker.hasLocationPermission() } returns false

        locationManager.isGpsAvailable.test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `isGpsAvailable emits updates from callback`() = runTest {
        every { permissionChecker.hasLocationPermission() } returns true
        val callbackSlot = slot<LocationCallback>()
        every {
            fusedLocationClient.requestLocationUpdates(any(), capture(callbackSlot), any())
        } returns mockk()

        locationManager.isGpsAvailable.test {
            val availability = mockk<LocationAvailability>()
            every { availability.isLocationAvailable } returns true

            callbackSlot.captured.onLocationAvailability(availability)

            assertTrue(awaitItem())
        }
    }

    @Test
    fun `locationUpdates emits updates from callback`() = runTest {
        every { permissionChecker.hasLocationPermission() } returns true
        val callbackSlot = slot<LocationCallback>()
        every {
            fusedLocationClient.requestLocationUpdates(any(), capture(callbackSlot), any())
        } returns mockk()

        locationManager.locationUpdates.test {
            val result = mockk<LocationResult>()
            val location = mockk<Location>()
            every { result.lastLocation } returns location

            callbackSlot.captured.onLocationResult(result)

            assertEquals(location, awaitItem())
        }
    }
}
