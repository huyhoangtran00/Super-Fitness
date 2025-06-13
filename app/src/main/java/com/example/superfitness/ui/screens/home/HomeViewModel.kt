package com.example.superfitness.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import com.example.superfitness.domain.usecases.run.RunUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val runUseCases: RunUseCases
): ViewModel() {
    /**
     * All runs list
     */
    val allRuns: StateFlow<List<RunEntity>> = runUseCases.getAllRuns()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}