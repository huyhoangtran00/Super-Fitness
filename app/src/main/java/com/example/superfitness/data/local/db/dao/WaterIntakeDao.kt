package com.example.superfitness.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.superfitness.data.local.db.entity.WaterIntake
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface WaterIntakeDao {

    // Thêm mới bản ghi
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(waterIntake: WaterIntake): Long

    // Cập nhật bản ghi
    @Update
    suspend fun update(waterIntake: WaterIntake): Int

    // Xóa bản ghi
    @Delete
    suspend fun delete(waterIntake: WaterIntake): Int

    // Lấy tất cả bản ghi theo ngày (sắp xếp mới nhất trước)
    @Query("SELECT * FROM water_intakes WHERE date = :date ORDER BY time DESC")
    fun getIntakesByDate(date: String): Flow<List<WaterIntake>>

    // Lấy tổng lượng nước đã uống trong ngày
    @Query("SELECT SUM(amount) FROM water_intakes WHERE date = :date")
    fun getDailyTotal(date: String): Flow<Int>

    // Lấy bản ghi cụ thể bằng ID
    @Query("SELECT * FROM water_intakes WHERE id = :id")
    suspend fun getIntakeById(id: Long): WaterIntake?

    // Lấy tất cả bản ghi (cho thống kê)
    @Query("SELECT * FROM water_intakes ORDER BY date DESC, time DESC")
    fun getAllIntakes(): Flow<List<WaterIntake>>

    // Xóa tất cả bản ghi trong ngày (debug/test)
    @Query("DELETE FROM water_intakes WHERE date = :date")
    suspend fun clearDailyIntakes(date: String)

    // Lấy các loại nước đã uống trong ngày
    @Query("SELECT DISTINCT type FROM water_intakes WHERE date = :date")
    suspend fun getDrinkTypesByDate(date: String): List<String>
}