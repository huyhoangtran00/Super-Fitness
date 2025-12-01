package com.example.superfitness.ui.screens.running_details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.usecases.run.RunUseCases
import com.example.superfitness.rules.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RunDetailsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private val runUseCases: RunUseCases = mockk(relaxed = true)

    @Test
    fun `runItem emits mapped RunDetails from use case`() = runTest {
        val runId = 1
        val runEntity =
                RunEntity(
                        id = runId,
                        timeStamp = 0L,
                        distance = 0,
                        duration = 0L,
                        pathPoints = "",
                        steps = 0L
                )
        every { savedStateHandle.get<Int>(RunDetailsDestination.runItemIdArg) } returns runId
        every { runUseCases.getRun(runId) } returns flowOf(runEntity)

        val viewModel = RunDetailsViewModel(savedStateHandle, runUseCases)

        viewModel.runItem.test {
            val item = awaitItem()
            assertEquals(runId, item.id)
        }
    }

    @Test
    fun `deleteRun calls useCase deleteRun`() = runTest {
        val runId = 1
        every { savedStateHandle.get<Int>(RunDetailsDestination.runItemIdArg) } returns runId
        val viewModel = RunDetailsViewModel(savedStateHandle, runUseCases)
        val runEntity = mockk<RunEntity>()

        viewModel.deleteRun(runEntity)

        coVerify { runUseCases.deleteRun(runEntity) }
    }
}
