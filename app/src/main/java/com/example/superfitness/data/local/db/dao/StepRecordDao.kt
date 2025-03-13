package com.example.superfitness.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.superfitness.data.local.db.entity.StepRecord

@Dao
interface StepRecordDao {

    // Phương thức Insert trả về Long (ID của bản ghi đã được chèn vào)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepRecord: StepRecord): Long  // Trả về ID của bản ghi mới chèn

    // Phương thức Update trả về Int (số lượng bản ghi bị thay đổi)
    @Update
    suspend fun update(stepRecord: StepRecord): Int  // Trả về số lượng bản ghi đã được cập nhật

    // Phương thức Delete trả về Int (số lượng bản ghi bị xóa)
    @Delete
    suspend fun delete(stepRecord: StepRecord): Int  // Trả về số lượng bản ghi bị xóa

    // Phương thức lấy bản ghi bước chân theo ngày, trả về kiểu Optional StepRecord
    @Query("SELECT * FROM step_records WHERE date = :date")
    suspend fun getStepRecordByDate(date: String): StepRecord?

    // Phương thức lấy tất cả bản ghi bước chân, trả về LiveData chứa danh sách StepRecord
    @Query("SELECT * FROM step_records")
    fun getAllStepRecords(): LiveData<List<StepRecord>>
}
