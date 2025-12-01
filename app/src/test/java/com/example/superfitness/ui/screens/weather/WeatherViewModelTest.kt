package com.example.superfitness.ui.screens.weather

import android.location.Location
import com.example.superfitness.domain.connectivity.ConnectivityObserver
import com.example.superfitness.domain.location.GeocoderHelper
import com.example.superfitness.domain.location.LocationManager
import com.example.superfitness.domain.models.Weather
import com.example.superfitness.domain.usecases.weather.GetWeatherUseCase
import com.example.superfitness.rules.MainDispatcherRule
import com.example.superfitness.utils.Response
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class WeatherViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private val locationManager: LocationManager = mockk()
    private val geocoderHelper: GeocoderHelper = mockk()
    private val connectivityObserver: ConnectivityObserver = mockk()

    @Test
    fun `fetchWeatherFromCurrentLocation updates state with weather data on success`() = runTest {
        val location = mockk<Location>()
        every { location.latitude } returns 10.0
        every { location.longitude } returns 20.0
        every { locationManager.locationUpdates } returns flowOf(location)
        every { connectivityObserver.isConnected } returns flowOf(true)
        coEvery { geocoderHelper.getAddressFromLocation(10.0, 20.0) } returns "Test Address"

        val weather = mockk<Weather>()
        every { getWeatherUseCase(10.0, 20.0) } returns flowOf(Response.Success(weather))

        val viewModel =
                WeatherViewModel(
                        getWeatherUseCase,
                        locationManager,
                        geocoderHelper,
                        connectivityObserver
                )

        val state = viewModel.weatherState
        assertEquals(weather, state.weather)
        assertEquals("Test Address", state.address)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `fetchWeatherFromCurrentLocation updates state with error when no internet`() = runTest {
        every { connectivityObserver.isConnected } returns flowOf(false)

        val viewModel =
                WeatherViewModel(
                        getWeatherUseCase,
                        locationManager,
                        geocoderHelper,
                        connectivityObserver
                )

        val state = viewModel.weatherState
        assertEquals("Check Internet connection", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `fetchWeatherFromCurrentLocation updates state with error when location is null`() =
            runTest {
                every { connectivityObserver.isConnected } returns flowOf(true)
                every { locationManager.locationUpdates } returns
                        flowOf() // Empty flow or null emission?
                every { locationManager.locationUpdates } returns flowOf() // Empty

                val viewModel =
                        WeatherViewModel(
                                getWeatherUseCase,
                                locationManager,
                                geocoderHelper,
                                connectivityObserver
                        )

                val state = viewModel.weatherState
                assertEquals("Unable to fetch location\nPlease check GPS settings", state.error)
                assertFalse(state.isLoading)
            }
}
