package com.example.superfitness.data.local.db.entity

import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.common.math.LongMath

@Entity(tableName = "run_records")
data class RunEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeStamp: Long,
    val distance: Int,
    val duration: Long,
    val pathPoints: String
)
