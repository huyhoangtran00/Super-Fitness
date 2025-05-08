package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.superfitness.repository.WaterIntakeRepository
import com.example.superfitness.viewmodel.WaterIntakeViewModel

class WaterIntakeViewModelFactory(private val waterIntakeRepository: WaterIntakeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra nếu modelClass là WaterIntakeViewModel
        if (modelClass.isAssignableFrom(WaterIntakeViewModel::class.java)) {
            return WaterIntakeViewModel(waterIntakeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
