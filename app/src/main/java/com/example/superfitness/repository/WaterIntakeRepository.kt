package com.example.superfitness.repository

import androidx.lifecycle.LiveData
import com.example.superfitness.data.local.db.dao.WaterIntakeDao
import com.example.superfitness.data.local.db.entity.WaterIntake

class WaterIntakeRepository(private val waterIntakeDao: WaterIntakeDao) {

    // Lấy tất cả lượng nước uống
    fun getAllWaterIntakes(): LiveData<List<WaterIntake>> = waterIntakeDao.getAllWaterIntakes()

    // Thêm lượng nước uống
    suspend fun insertWaterIntake(waterIntake: WaterIntake) {
        waterIntakeDao.insert(waterIntake)
    }

    // Lấy lượng nước uống theo ngày
    suspend fun getWaterIntakeByDate(date: String): WaterIntake? {
        return waterIntakeDao.getWaterIntakeByDate(date)
    }

    // Xóa lượng nước uống
    suspend fun deleteWaterIntake(waterIntake: WaterIntake) {
        waterIntakeDao.delete(waterIntake)
    }
}
