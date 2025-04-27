package com.example.superfitness.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.superfitness.data.local.db.entity.WaterIntake
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WaterHistory(recentWaterIntakes: List<WaterIntake>) {
    if (recentWaterIntakes.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Lịch sử uống nước",
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
                recentWaterIntakes.forEachIndexed { index, intake ->
                    WaterHistoryItem(intake)
                    if (index < recentWaterIntakes.size - 1) {
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
fun WaterHistoryItem(intake: WaterIntake) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = "Water Intake",
            tint = Color.Blue,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${intake.amount} ml",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = intake.type,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                text = "${intake.time}   ${intake.amount} ml",
                color = Color.Black,
                fontSize = 12.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateFormatInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateFormatOutput = SimpleDateFormat("dd/M/yy", Locale.getDefault())
            val formattedDate = try {
                val date = dateFormatInput.parse(intake.date)
                dateFormatOutput.format(date!!)
            } catch (e: Exception) {
                intake.date
            }

            Text(
                text = formattedDate,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}