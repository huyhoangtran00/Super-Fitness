package com.example.superfitness.ui.screens.run.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EditMessage(msg: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (msg == "GPS is unavailable") Color.Red else Color.Green,
        shadowElevation = 18.dp
    ) {
        Text(
            text = msg,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}