package com.example.superfitness.ui.screens.run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.superfitness.utils.RED

@Composable
fun StartScreenBottomBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    BottomAppBar(
        content = {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    shadowElevation = 4.dp
                ) {
                    TextButton(
                        modifier = Modifier
                            .size(80.dp),
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(RED.toColorInt()))
                    ) {
                        Text(
                            text = "START",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = modifier.height(120.dp)
    )
}