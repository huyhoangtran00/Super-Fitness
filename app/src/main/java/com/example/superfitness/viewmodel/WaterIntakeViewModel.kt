package com.example.superfitness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.superfitness.repository.WaterIntakeRepository
import com.example.superfitness.data.local.db.entity.WaterIntake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WaterIntakeViewModel(private val repository: WaterIntakeRepository) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Get all intakes as Flow converted to LiveData
    val allIntakes = repository.getAllIntakesFlow().asLiveData()

    // Get today's intakes
    fun getTodayIntakes() = repository.getIntakesByDateFlow(getCurrentDate()).asLiveData()

    // Get intakes for specific date
    fun getIntakesByDate(date: String) = repository.getIntakesByDateFlow(date).asLiveData()

    // Get today's total water intake
    val todayTotal = repository.getDailyTotalFlow(getCurrentDate()).asLiveData()

    // Get total for specific date
    fun getDailyTotal(date: String) = repository.getDailyTotalFlow(date).asLiveData()

    // Add new water intake
    fun addIntake(amount: Int, type: String, customDate: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addIntake(amount, type, customDate)
        }
    }

    // Update existing intake
    fun updateIntake(intake: WaterIntake) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIntake(intake)
        }
    }

    // Delete intake
    fun deleteIntake(intake: WaterIntake) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteIntake(intake)
        }
    }

    // Get drink types for today
    fun getTodayDrinkTypes() = viewModelScope.launch(Dispatchers.IO) {
        repository.getDrinkTypesByDate(getCurrentDate())
    }

    // Get drink types for specific date
    fun getDrinkTypesByDate(date: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.getDrinkTypesByDate(date)
    }

    // Clear today's records
    fun clearTodayRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearDayRecords(getCurrentDate())
        }
    }

    // Clear records for specific date
    fun clearDateRecords(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearDayRecords(date)
        }
    }

    private fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }
}