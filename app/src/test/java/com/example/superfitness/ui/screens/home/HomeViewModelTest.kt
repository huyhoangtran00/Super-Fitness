package com.example.superfitness.ui.screens.home

import app.cash.turbine.test
import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.usecases.run.RunUseCases
import com.example.superfitness.rules.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val runUseCases: RunUseCases = mockk(relaxed = true)

    @Test
    fun `allRuns emits list of runs from use case`() = runTest {
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
        every { runUseCases.getAllRuns() } returns flowOf(runList)

        val viewModel = HomeViewModel(runUseCases)

        viewModel.allRuns.test { assertEquals(runList, awaitItem()) }
    }
}
