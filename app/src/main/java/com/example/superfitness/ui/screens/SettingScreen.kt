package com.example.superfitness.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.asFlow
import com.example.superfitness.data.local.db.entity.StepRecord
import com.example.superfitness.ui.charts.BarChart
import com.example.superfitness.ui.charts.RunData
import com.example.superfitness.ui.charts.RunHistory
import com.example.superfitness.ui.charts.WaterData
import com.example.superfitness.ui.viewmodel.StepRecordViewModel
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun SettingScreen(
    stepRecordViewModel: StepRecordViewModel,
    waterIntakeViewModel: WaterIntakeViewModel
) {
    // Lấy 7 ngày gần nhất
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val recentDays = (0 downTo -6).map { offset ->
        calendar.apply { add(Calendar.DAY_OF_YEAR, offset) }
        val date = dateFormat.format(calendar.time)
        calendar.apply { add(Calendar.DAY_OF_YEAR, -offset) }
        date
    }.reversed()

    // Lấy dữ liệu bước chân cho biểu đồ
    val stepRecords by stepRecordViewModel.getAllStepRecords().asFlow()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val stepData = recentDays.map { date ->
        val record = stepRecords.find { it.date == date }
        RunData(
            date = date,
            value = record?.distance ?: 0f
        )
    }

    // Lấy dữ liệu nước uống
    val waterIntakes by waterIntakeViewModel.allIntakes.asFlow()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val waterData = recentDays.map { date ->
        val intakes = waterIntakes.filter { it.date == date }
        WaterData(
            date = date,
            value = if (intakes.isNotEmpty()) intakes.sumOf { it.amount }.toFloat() / 1000f else 0f
        )
    }

    // Lấy bản ghi của 7 ngày gần nhất (không lọc distance > 0)
    val recentStepRecords = recentDays.map { date ->
        stepRecords.find { it.date == date } ?: StepRecord(
            date = date,
            steps = 0,
            distance = 0f,
            calories = 0f,
            duration = "00:00:00"
        )
    }.sortedByDescending { it.date }

    if (stepRecords.isEmpty() && waterIntakes.isEmpty()) {
        // Hiển thị màn hình chờ nếu dữ liệu chưa sẵn sàng
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color.Green // Tùy chỉnh màu của loading indicator
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Đang tải dữ liệu...",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    } else {
        // Hiển thị giao diện chính khi dữ liệu đã sẵn sàng
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hiển thị biểu đồ
            BarChart(
                stepData = stepData,
                waterData = waterData
            )

            // Thêm khoảng cách giữa BarChart và RunHistory
            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị lịch sử chạy bộ
            RunHistory(recentStepRecords = recentStepRecords)

            Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách dưới cùng
        }
    }
}