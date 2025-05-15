package com.example.superfitness.ui.screens

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.superfitness.R
import com.example.superfitness.ui.charts.BarChart
import com.example.superfitness.ui.charts.RunData
import com.example.superfitness.ui.charts.RunHistory
import com.example.superfitness.ui.charts.WaterData
import com.example.superfitness.ui.charts.WaterHistory
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel
import com.example.superfitness.viewmodel.RunViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object BarScreenDestination : NavigationDestination {
    override val route = "bar_screen"
    override val titleRes = R.string.activity_stats
}

@Composable
fun BarScreen(
    runViewModel: RunViewModel,
    waterIntakeViewModel: WaterIntakeViewModel
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val sevenDaysAgo = calendar.apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis

    val today = Calendar.getInstance()
    val recentDays = (0..6).map { i ->
        val cal = today.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, -i)
        dateFormat.format(cal.time)
    }.reversed()

    val runs by runViewModel.getRunsStream()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val recentRuns = runs.filter { it.timeStamp >= sevenDaysAgo }
        .sortedByDescending { it.timeStamp }

    val runsByDay = recentDays.map { day ->
        val dayStart = dateFormat.parse(day)?.time ?: 0L
        val dayEnd = dayStart + TimeUnit.DAYS.toMillis(1) - 1

        val dayRuns = recentRuns.filter { it.timeStamp in dayStart..dayEnd }

        val totalDistance = if (dayRuns.isNotEmpty()) {
            dayRuns.sumOf { it.distance }.toFloat() / 1000f
        } else 0f

        RunData(date = day, value = totalDistance)
    }

    val allWaterIntakes by waterIntakeViewModel.getAllIntakesFlow()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val waterData = recentDays.map { date ->
        val intakes = allWaterIntakes.filter { it.date == date }
        WaterData(
            date = date,
            value = if (intakes.isNotEmpty()) intakes.sumOf { it.amount }.toFloat() else 0f
        )
    }

    val recentWaterIntakes = allWaterIntakes.sortedByDescending { it.date + " " + it.time }.take(10)

    var isRunDataSelected by remember { mutableStateOf(true) }

    if (runs.isEmpty() && allWaterIntakes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.Green)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Đang tải dữ liệu...", color = Color.White, fontSize = 16.sp)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            BarChart(
                runData = runsByDay,
                waterData = waterData,
                onSelectionChange = { isSelected ->
                    isRunDataSelected = isSelected
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isRunDataSelected) {
                RunHistory(recentRunRecords = recentRuns)
            } else {
                WaterHistory(recentWaterIntakes = recentWaterIntakes)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
