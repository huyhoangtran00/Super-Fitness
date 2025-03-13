package com.example.superfitness.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: Long, // Timestamp
    val type: String,
    val status: Boolean = true,

)