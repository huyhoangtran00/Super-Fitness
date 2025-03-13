package com.example.superfitness.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_records")
data class StepRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,        // Định dạng: "yyyy-MM-dd"
    val steps: Int,
    val distance: Float,     // Số km đã đi
    val calories: Float
)
