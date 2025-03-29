package com.example.superfitness.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// Data class to hold fitness data
data class FitnessData(
    val date: String, // "dd/MM/yyyy"
    val hour: Int // Số giờ tập luyện
)
@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Box(
        modifier = Modifier
            .weight(weight)
            .border(1.dp, Color.Black)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun WorkoutCalendar(fitnessDataList: List<FitnessData>) {
    val today = LocalDate.now()
    val yearMonth = YearMonth.of(today.year, today.month)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7 // Adjust to match Sun-Sat
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val fitnessMap = fitnessDataList.associateBy { LocalDate.parse(it.date, formatter).dayOfMonth }
    val dayWeight = 1f / 7f

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Lịch tập luyện tháng ${today.month.value}/${today.year}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(modifier = Modifier.border(1.dp, Color.Black).padding(4.dp)) {
            // Row for day labels
            Row(Modifier.background(Color.Gray).fillMaxWidth()) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    TableCell(text = it, weight = dayWeight)
                }
            }

            var dayCount = 1
            for (week in 0..5) {
                Row(Modifier.fillMaxWidth()) {
                    for (day in 0..6) {
                        if (week == 0 && day < firstDayOfMonth || dayCount > daysInMonth) {
                            TableCell(text = "", weight = dayWeight)
                        } else {
                            val dayData = fitnessMap[dayCount]
                            val backgroundColor = when {
                                dayCount < today.dayOfMonth && (dayData == null || dayData.hour == 0)-> Color.Red
                                dayCount < today.dayOfMonth && dayData?.hour != 0 -> Color.Green
                                dayCount >= today.dayOfMonth -> Color.White
                                else -> Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .weight(dayWeight)
                                    .background(backgroundColor)
                                    .border(1.dp, Color.Black)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = dayCount.toString(), fontSize = 14.sp, color = Color.Black)
                            }
                            dayCount++
                        }
                    }
                }
            }
        }
    }
}
