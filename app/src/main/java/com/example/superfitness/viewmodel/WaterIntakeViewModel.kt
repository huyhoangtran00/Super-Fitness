package com.example.superfitness.ui.viewmodel

import kotlin.text.Typography.dagger


import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.superfitness.data.local.db.entity.WaterIntake
import com.example.superfitness.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.launch
import javax.inject.Inject


class WaterIntakeViewModel (
    private val repository: WaterIntakeRepository
) : ViewModel() {

    private val currentDate = MutableStateFlow(repository.getCurrentDate())

    val dailyTotal: StateFlow<Int> = repository.getDailyTotalFlow()
        .combine(currentDate) { total, _ -> total ?: 0 }
        .stateInDefault(0)


    val intakesByDate: StateFlow<List<WaterIntake>> = repository.getIntakesByDateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // Các loại đồ uống trong ngày
   // val drinkTypes = repository.getDrinkTypesByDate().asLiveData()

    fun addIntake(amount: Int, type: String) {
        viewModelScope.launch {
            repository.addIntake(amount, type)
        }
    }

    fun updateIntake(waterIntake: WaterIntake) {
        viewModelScope.launch {
            repository.updateIntake(waterIntake)
        }
    }

    fun deleteIntake(waterIntake: WaterIntake) {
        viewModelScope.launch {
            repository.deleteIntake(waterIntake)
        }
    }

    fun getIntakeById(id: Long) {
        viewModelScope.launch {
            repository.getIntakeById(id)
        }
    }

    fun refreshDataForDate(date: String? = null) {
        currentDate.value = date ?: repository.getCurrentDate()
    }

    private fun <T> Flow<T>.stateInDefault(initialValue: T): StateFlow<T> {
        return this.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly, // hoặc SharingStarted.Lazily
            initialValue = initialValue
        )
    }

}