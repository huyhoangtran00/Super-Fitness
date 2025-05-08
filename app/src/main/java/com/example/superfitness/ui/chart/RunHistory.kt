package com.example.superfitness.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superfitness.data.local.db.entity.RunEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RunHistory(recentRunRecords: List<RunEntity>) {
    if (recentRunRecords.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(
                    elevation = 8.dp, // Độ cao của bóng
                    shape = RoundedCornerShape(16.dp), // Bo góc của bóng
                    clip = true // Đảm bảo bóng không bị cắt
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Lịch sử chạy bộ",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                recentRunRecords.forEachIndexed { index, record ->
                    RunningHistoryItem(record)
                    if (index < recentRunRecords.size - 1) {
                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RunningHistoryItem(record: RunEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsRun,
            contentDescription = "Running",
            tint = Color.Green,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Convert distance from meters to kilometers
            val distanceInKm = record.distance / 1000f
            
            Text(
                text = "${String.format("%.1f", distanceInKm)} km",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Chạy bộ",
                color = Color.Black,
                fontSize = 12.sp
            )

            // Format duration from milliseconds to HH:mm:ss
            val durationFormatted = formatDuration(record.duration)
            // Calculate calories (simple placeholder calculation)
            val caloriesBurned = (distanceInKm * 65).toInt() // Simple estimation
            
            Text(
                text = "$durationFormatted   ${String.format("%.1f", distanceInKm)} km   $caloriesBurned calo",
                color = Color.Black,
                fontSize = 12.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Format timestamp to date
            val date = Date(record.timeStamp)
            val dateFormat = SimpleDateFormat("dd/M/yy", Locale.getDefault())
            val formattedDate = dateFormat.format(date)

            Text(
                text = formattedDate,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

// Helper function to format milliseconds to HH:mm:ss
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
