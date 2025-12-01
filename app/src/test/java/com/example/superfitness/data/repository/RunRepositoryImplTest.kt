package com.example.superfitness.data.repository

import com.example.superfitness.data.local.dao.RunDao
import com.example.superfitness.domain.models.entity.RunEntity
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RunRepositoryImplTest {

    private val runDao: RunDao = mockk(relaxed = true)
    private val repository = OfflineRunRepository(runDao)

    @Test
    fun `getRunStream returns flow from dao`() = runTest {
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
        every { runDao.getRun(id) } returns flowOf(runEntity)

        val result = repository.getRunStream(id)

        result.collect { assertEquals(runEntity, it) }
        verify { runDao.getRun(id) }
    }

    @Test
    fun `getRunsStream returns flow list from dao`() = runTest {
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
        every { runDao.getAllRuns() } returns flowOf(runList)

        val result = repository.getRunsStream()

        result.collect { assertEquals(runList, it) }
        verify { runDao.getAllRuns() }
    }

    @Test
    fun `addRun calls dao insertRun`() = runTest {
        val runEntity =
                RunEntity(
                        id = 1,
                        timeStamp = 0L,
                        distance = 0,
                        duration = 0L,
                        pathPoints = "",
                        steps = 0L
                )

        repository.addRun(runEntity)

        coVerify { runDao.insertRun(runEntity) }
    }

    @Test
    fun `removeRun calls dao deleteRun`() = runTest {
        val runEntity =
                RunEntity(
                        id = 1,
                        timeStamp = 0L,
                        distance = 0,
                        duration = 0L,
                        pathPoints = "",
                        steps = 0L
                )

        repository.removeRun(runEntity)

        coVerify { runDao.deleteRun(runEntity) }
    }
}
