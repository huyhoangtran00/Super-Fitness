package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.data.local.db.entity.RunEntity
import com.example.superfitness.repository.RunRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val runRepository: RunRepository
): ViewModel() {
    /**
     * All runs list
     */
    val allRuns: StateFlow<List<RunEntity>> = runRepository.getRunsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Get the selected run
     */
    suspend fun getRun(id: Int): RunEntity? {
        return runRepository
            .getRun(id)
            .firstOrNull()
    }

    /**
     * Delete a run from database
     */
    fun deleteRun(runEntity: RunEntity) {
        viewModelScope.launch {
            runRepository.removeRun(runEntity)
        }
    }
}