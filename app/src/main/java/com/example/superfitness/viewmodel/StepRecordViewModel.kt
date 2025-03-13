package com.example.superfitness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.superfitness.repository.StepRecordRepository
import com.example.superfitness.data.local.db.entity.StepRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StepRecordViewModel(private val stepRecordRepository: StepRecordRepository) : ViewModel() {

    // Lấy tất cả các bản ghi bước chân từ repository
    fun getAllStepRecords(): LiveData<List<StepRecord>> {
        return stepRecordRepository.getAllStepRecords()
    }

    // Thêm bản ghi bước chân vào repository
    fun insertStepRecord(stepRecord: StepRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            stepRecordRepository.insertStepRecord(stepRecord)
        }
    }

    // Lấy bản ghi bước chân theo ngày
    fun getStepRecordByDate(date: String): LiveData<StepRecord?> {
        return liveData(Dispatchers.IO) {
            emit(stepRecordRepository.getStepRecordByDate(date))
        }
    }

    // Xóa bản ghi bước chân
    fun deleteStepRecord(stepRecord: StepRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            stepRecordRepository.deleteStepRecord(stepRecord)
        }
    }
}
