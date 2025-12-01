package com.example.superfitness.ui.screens.run

import android.location.Location
import app.cash.turbine.test
import com.example.superfitness.domain.location.LocationManager
import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.usecases.run.RunUseCases
import com.example.superfitness.rules.MainDispatcherRule
import com.google.android.gms.maps.model.LatLng
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RunViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val locationManager: LocationManager = mockk(relaxed = true)
    private val runUseCases: RunUseCases = mockk(relaxed = true)

    @Test
    fun `isGpsAvailable emits value from locationManager`() = runTest {
        every { locationManager.isGpsAvailable } returns flowOf(true)

        val viewModel = RunViewModel(locationManager, runUseCases)

        viewModel.isGpsAvailable.test { assertEquals(true, awaitItem()) }
    }

    @Test
    fun `currentLocation emits mapped LatLng from locationManager`() = runTest {
        val location = mockk<Location>()
        every { location.latitude } returns 10.0
        every { location.longitude } returns 20.0
        every { locationManager.locationUpdates } returns flowOf(location)

        val viewModel = RunViewModel(locationManager, runUseCases)

        viewModel.currentLocation.test {
            val item = awaitItem()
            assertEquals(LatLng(10.0, 20.0), item)
        }
    }

    @Test
    fun `addRun calls useCase addRun`() = runTest {
        val viewModel = RunViewModel(locationManager, runUseCases)
        val runEntity =
                RunEntity(
                        id = 1,
                        timeStamp = 0L,
                        distance = 0,
                        duration = 0L,
                        pathPoints = "",
                        steps = 0L
                )

        viewModel.addRun(runEntity)

        coVerify { runUseCases.addRun(runEntity) }
    }
}
