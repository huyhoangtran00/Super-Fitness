package com.example.superfitness.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superfitness.ui.WaterIntakePieChart

@Composable
fun WaterTrackerScreen() {
    val drunkWater = 1200f
    val requiredWater = 2000f

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Lượng nước đã uống", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        WaterIntakePieChart(drunkWater, requiredWater)
    }
}
