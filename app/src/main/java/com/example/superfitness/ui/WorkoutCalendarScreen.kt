package com.example.superfitness.ui

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkoutCalendarScreen(apiData: List<RunningData>) {
    val today = Calendar.getInstance()
    val currentDay = today.get(Calendar.DAY_OF_MONTH)
    val currentMonth = today.get(Calendar.MONTH)
    val currentYear = today.get(Calendar.YEAR)

    val daysInMonth = getDaysInMonth(currentYear, currentMonth) // Lấy danh sách số ngày trong tháng
    val workoutDays = apiData.map { it.day } // Lấy danh sách ngày có tập luyện

    val weeks = splitWeeks(daysInMonth, currentYear, currentMonth)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tháng ${currentMonth + 1}, $currentYear",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            color = Color.Black
        )
        Row(
            modifier = Modifier.border(1.dp, color = Color.Black),
        ) {
            listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f).border(1.dp, color = Color.Black)
                        ,
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = day,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(4.dp),
                        color = Color.Black
                    )
                }
            }
        }

        // Hiển thị các tuần
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                week.forEach { day ->
                    val formattedDate = if (day != null) SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Calendar.getInstance().apply {
                        set(currentYear, currentMonth, day)
                    }.time) else null

                    val isWorkoutDay = formattedDate != null && workoutDays.contains(formattedDate)

                    val bgColor = when {
                        day == null -> Color.Transparent // Ngày trống đầu tuần
                        day > currentDay -> Color.White  // Ngày tương lai
                        isWorkoutDay -> Color.Green      // Ngày có tập
                        else -> Color.Red               // Ngày không tập
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = bgColor, shape = RectangleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            Text(
                                text = day.toString(),
                                color = if (bgColor == Color.White) Color.Black else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// Chia tháng thành các tuần, mỗi tuần bắt đầu từ thứ 2
fun splitWeeks(daysInMonth: List<Int>, year: Int, month: Int): List<List<Int?>> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // Thứ của ngày đầu tiên trong tháng
    val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2 // Điều chỉnh để bắt đầu từ T2

    val weeks = mutableListOf<List<Int?>>()
    val currentWeek = mutableListOf<Int?>()

    // Thêm ngày trống vào đầu tuần nếu tháng không bắt đầu từ T2
    repeat(offset) { currentWeek.add(null) }

    // Thêm ngày vào lịch
    for (day in daysInMonth) {
        currentWeek.add(day)
        if (currentWeek.size == 7) {
            weeks.add(currentWeek.toList())
            currentWeek.clear()
        }
    }

    // Nếu tuần cuối cùng chưa đủ 7 ngày, thêm null vào
    if (currentWeek.isNotEmpty()) {
        while (currentWeek.size < 7) currentWeek.add(null)
        weeks.add(currentWeek.toList())
    }

    return weeks
}

// Lấy số ngày trong tháng
fun getDaysInMonth(year: Int, month: Int): List<Int> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return (1..maxDay).toList()
}

data class RunningData(val day: String, val runningHours: Int)

