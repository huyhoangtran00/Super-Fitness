package com.example.superfitness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.superfitness.repository.WaterIntakeRepository
import com.example.superfitness.data.local.db.entity.WaterIntake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WaterIntakeViewModel(private val waterIntakeRepository: WaterIntakeRepository) : ViewModel() {

    // Lấy tất cả lượng nước uống từ repository
    fun getAllWaterIntakes(): LiveData<List<WaterIntake>> {
        return waterIntakeRepository.getAllWaterIntakes()
    }

    // Thêm lượng nước uống vào repository
    fun insertWaterIntake(waterIntake: WaterIntake) {
        viewModelScope.launch(Dispatchers.IO) {
            waterIntakeRepository.insertWaterIntake(waterIntake)
        }
    }

    // Lấy lượng nước uống theo ngày
    fun getWaterIntakeByDate(date: String): LiveData<WaterIntake?> {
        return liveData(Dispatchers.IO) {
            emit(waterIntakeRepository.getWaterIntakeByDate(date))
        }
    }

    // Xóa lượng nước uống
    fun deleteWaterIntake(waterIntake: WaterIntake) {
        viewModelScope.launch(Dispatchers.IO) {
            waterIntakeRepository.deleteWaterIntake(waterIntake)
        }
    }
}
