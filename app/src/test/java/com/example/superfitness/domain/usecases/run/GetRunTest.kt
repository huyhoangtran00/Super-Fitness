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

class GetRunTest {

    private val repository: RunRepository = mockk()
    private val getRun = GetRun(repository)

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val id = 1
        val runEntity =
                RunEntity(
                        id = id,
                        timeStamp = 0L,
                        distance = 0,
                        duration = 0L,
                        pathPoints = "",
                        steps = 0L
                )
        every { repository.getRunStream(id) } returns flowOf(runEntity)

        getRun(id).test {
            assertEquals(runEntity, awaitItem())
            awaitComplete()
        }

        verify { repository.getRunStream(id) }
    }
}
