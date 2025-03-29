package com.example.superfitness.ui

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun WaterIntakePieChart(drunkWater: Float, requiredWater: Float) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setUsePercentValues(true)
                holeRadius = 50f
                transparentCircleRadius = 55f
                setEntryLabelColor(Color.BLACK)
                legend.isEnabled = false
            }
        },
        update = { pieChart ->
            val entries = mutableListOf<PieEntry>().apply {
                add(PieEntry(drunkWater, "Đã uống"))
                add(PieEntry(requiredWater - drunkWater, "Còn lại"))
            }

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(Color.BLUE, Color.LTGRAY)
                valueTextSize = 14f
            }

            pieChart.data = PieData(dataSet)
            pieChart.invalidate()
        }
    )
}