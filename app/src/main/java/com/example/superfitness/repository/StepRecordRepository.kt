package com.example.superfitness.repository

import androidx.lifecycle.LiveData
import com.example.superfitness.data.local.db.dao.StepRecordDao
import com.example.superfitness.data.local.db.entity.StepRecord
import com.example.superfitness.data.local.db.entity.WaterIntake

class StepRecordRepository(private val stepRecordDao: StepRecordDao) {

    // Lấy tất cả các bản ghi bước chân
    fun getAllStepRecords(): LiveData<List<StepRecord>> = stepRecordDao.getAllStepRecords()

    // Thêm bản ghi bước chân
    suspend fun insertStepRecord(stepRecord: StepRecord) {
        stepRecordDao.insert(stepRecord)
    }

    // Lấy bản ghi bước chân theo ngày
    suspend fun getStepRecordByDate(date: String): StepRecord? {
        return stepRecordDao.getStepRecordByDate(date)
    }

    suspend fun deleteStepRecord(stepRecord: StepRecord) {
        stepRecordDao.delete(stepRecord)
    }
}