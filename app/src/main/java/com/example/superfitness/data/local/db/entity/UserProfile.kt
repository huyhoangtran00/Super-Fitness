package com.example.superfitness.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Mỗi người dùng chỉ có một hồ sơ, dùng id cố định 1
    val name: String,
    val age: Int,
    val gender: String,
    val height: Float,
    val weight: Float,
    val bmi: Float,
    val goal: String
)


