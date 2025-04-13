package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.superfitness.repository.WaterIntakeRepository
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel

class WaterIntakeViewModelFactory(private val repository: WaterIntakeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterIntakeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterIntakeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}