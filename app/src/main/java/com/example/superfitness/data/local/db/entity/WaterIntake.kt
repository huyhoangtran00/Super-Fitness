package com.example.superfitness.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "water_intakes")  // Đổi tên table số nhiều
data class WaterIntake(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // Sử dụng Long
    val amount: Int,                // Lượng nước (ml)
    val type: String,               // Loại nước ("Nước lọc", "Trà", etc.)
    val time: String,               // Thời gian (HH:mm)
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // Ngày tự động
) {
    companion object {
        const val DEFAULT_TYPE = "Nước lọc"
    }
}