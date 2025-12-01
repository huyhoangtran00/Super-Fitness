package com.example.superfitness.domain.usecases.run

import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteRunTest {

    private val repository: RunRepository = mockk(relaxed = true)
    private val deleteRun = DeleteRun(repository)

    @Test
    fun `invoke calls repository removeRun`() = runTest {
        val runEntity =
                RunEntity(
                        id = 1,
                        timeStamp = 0L,
                        distance = 0,
                        duration = 0L,
                        pathPoints = "",
                        steps = 0L
                )

        deleteRun(runEntity)

        coVerify { repository.removeRun(runEntity) }
    }
}
