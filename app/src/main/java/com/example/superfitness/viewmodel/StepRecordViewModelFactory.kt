package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.superfitness.repository.StepRecordRepository
import com.example.superfitness.ui.viewmodel.StepRecordViewModel

class StepRecordViewModelFactory(private val repository: StepRecordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StepRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}