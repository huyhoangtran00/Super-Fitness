package com.example.superfitness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.superfitness.data.local.db.dao.WaterIntakeDao
import com.example.superfitness.data.local.db.entity.WaterIntake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class WaterIntakeRepository(private val waterIntakeDao: WaterIntakeDao) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Lấy tất cả bản ghi (Flow)
    fun getAllIntakesFlow(): Flow<List<WaterIntake>> = waterIntakeDao.getAllIntakes()

    // Lấy tất cả bản ghi (LiveData - nếu cần cho legacy code)
    fun getAllIntakesLiveData(): LiveData<List<WaterIntake>> {
        return waterIntakeDao.getAllIntakes().asLiveData()
    }
    // Lấy bản ghi theo ngày (Flow)
    fun getIntakesByDateFlow(date: String = getCurrentDate()): Flow<List<WaterIntake>> {
        return waterIntakeDao.getIntakesByDate(date)
    }

    // Lấy tổng lượng nước theo ngày (Flow)
    fun getDailyTotalFlow(date: String = getCurrentDate()): Flow<Int> {
        return waterIntakeDao.getDailyTotal(date)
            .map { it ?: 0 } // Nếu null, thay bằng 0
    }
    // Thêm bản ghi mới (tiện ích mở rộng)
    suspend fun addIntake(amount: Int, type: String, customDate: String? = null) {
        waterIntakeDao.insert(
            WaterIntake(
                amount = amount,
                type = type,
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                date = customDate ?: getCurrentDate()
            )
        )
    }

    // Cập nhật bản ghi
    suspend fun updateIntake(waterIntake: WaterIntake): Int {
        return waterIntakeDao.update(waterIntake)
    }

    // Xóa bản ghi
    suspend fun deleteIntake(waterIntake: WaterIntake): Int {
        return waterIntakeDao.delete(waterIntake)
    }

    // Lấy bản ghi bằng ID
    suspend fun getIntakeById(id: Long): WaterIntake? {
        return waterIntakeDao.getIntakeById(id)
    }

    // Lấy các loại nước đã uống trong ngày
    suspend fun getDrinkTypesByDate(date: String = getCurrentDate()): List<String> {
        return waterIntakeDao.getDrinkTypesByDate(date)
    }

    // Xóa tất cả bản ghi trong ngày (cho mục đích debug/test)
    suspend fun clearDayRecords(date: String = getCurrentDate()) {
        waterIntakeDao.clearDailyIntakes(date)
    }

    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }
}