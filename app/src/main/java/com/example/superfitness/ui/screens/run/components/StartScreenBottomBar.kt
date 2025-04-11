package com.example.superfitness.ui.screens.run.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                TextButton(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(80.dp),
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(text = "START", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        modifier = modifier
    )
}