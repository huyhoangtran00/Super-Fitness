package com.example.superfitness.domain.usecases.weather

import app.cash.turbine.test
import com.example.superfitness.data.mapper_impl.ApiWeatherMapper
import com.example.superfitness.data.remote.models.ApiWeather
import com.example.superfitness.domain.models.Weather
import com.example.superfitness.domain.repository.WeatherRepository
import com.example.superfitness.utils.Response
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response as RetrofitResponse

class GetWeatherUseCaseTest {

    private val repository: WeatherRepository = mockk()
    private val apiWeatherMapper: ApiWeatherMapper = mockk()
    private val getWeatherUseCase = GetWeatherUseCase(repository, apiWeatherMapper)

    @Test
    fun `invoke emits Loading then Success when repository returns data`() = runTest {
        val lat = 10.0
        val long = 20.0
        val apiWeather = mockk<ApiWeather>()
        val domainWeather = mockk<Weather>()

        coEvery { repository.getWeatherData(lat, long) } returns apiWeather
        every { apiWeatherMapper.mapToDomain(apiWeather) } returns domainWeather

        getWeatherUseCase(lat, long).test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Response.Loading)

            val secondEmission = awaitItem()
            assertTrue(secondEmission is Response.Success)
            assertEquals(domainWeather, secondEmission.data)

            awaitComplete()
        }
    }

    @Test
    fun `invoke emits Loading then Error when repository throws IOException`() = runTest {
        val lat = 10.0
        val long = 20.0

        coEvery { repository.getWeatherData(lat, long) } throws IOException()

        getWeatherUseCase(lat, long).test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Response.Loading)

            val secondEmission = awaitItem()
            assertTrue(secondEmission is Response.Error)
            assertEquals("Check Internet Connection", secondEmission.message)

            awaitComplete()
        }
    }

    @Test
    fun `invoke emits Loading then Error when repository throws HttpException`() = runTest {
        val lat = 10.0
        val long = 20.0
        val response = RetrofitResponse.error<Any>(500, "".toResponseBody())

        coEvery { repository.getWeatherData(lat, long) } throws HttpException(response)

        getWeatherUseCase(lat, long).test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Response.Loading)

            val secondEmission = awaitItem()
            assertTrue(secondEmission is Response.Error)
            assertEquals("Http error", secondEmission.message)

            awaitComplete()
        }
    }
}
