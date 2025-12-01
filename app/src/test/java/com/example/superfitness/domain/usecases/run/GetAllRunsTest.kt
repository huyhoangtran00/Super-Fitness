package com.example.superfitness.domain.usecases.run

import app.cash.turbine.test
import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllRunsTest {

    private val repository: RunRepository = mockk()
    private val getAllRuns = GetAllRuns(repository)

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val runEntity =
                RunEntity(
                        id = 1,
                        timeStamp = 0L,
                        distance = 0,
                        duration = 0L,
                        pathPoints = "",
                        steps = 0L
                )
        val runList = listOf(runEntity)
        every { repository.getRunsStream() } returns flowOf(runList)

        getAllRuns().test {
            assertEquals(runList, awaitItem())
            awaitComplete()
        }

        verify { repository.getRunsStream() }
    }
}
