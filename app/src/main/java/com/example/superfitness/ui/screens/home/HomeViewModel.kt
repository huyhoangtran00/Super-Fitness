package com.example.superfitness.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.data.local.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val runRepository: RunRepository
): ViewModel() {
    /**
     * All runs list
     */
    val allRuns: StateFlow<List<RunEntity>> = runRepository.getRunsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}