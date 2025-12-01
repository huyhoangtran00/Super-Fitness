package com.example.superfitness.data.repository

import com.example.superfitness.data.remote.WeatherApi
import com.example.superfitness.data.remote.models.ApiWeather
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherRepositoryImplTest {

    private val weatherApi: WeatherApi = mockk()
    private val repository = WeatherRepositoryImpl(weatherApi)

    @Test
    fun `getWeatherData returns data from api`() = runTest {
        val lat = 10.0
        val long = 20.0
        val apiWeather = mockk<ApiWeather>()

        coEvery { weatherApi.getWeatherData(lat, long) } returns apiWeather

        val result = repository.getWeatherData(lat, long)

        assertEquals(apiWeather, result)
    }
}
