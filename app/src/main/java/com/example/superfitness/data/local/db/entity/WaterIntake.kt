package com.example.superfitness.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_intake")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,       // Định dạng: "yyyy-MM-dd"
    val totalMl: Int,       // Lượng nước đã uống (ml)
    val goalMl: Int         // Mục tiêu uống nước (ml)
)
