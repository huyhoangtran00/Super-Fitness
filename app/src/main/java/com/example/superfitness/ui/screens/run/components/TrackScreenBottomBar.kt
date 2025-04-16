package com.example.superfitness.ui.screens.run.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.superfitness.utils.RED

@Composable
fun TrackScreenBottomBar(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    onPauseAndResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    BottomAppBar(
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                // Pause and Resume button
                Surface(
                    shape = CircleShape,
                    shadowElevation = 4.dp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    IconButton(
                        onClick = onPauseAndResumeClick,
                        colors = if (isRunning) {
                            IconButtonDefaults.iconButtonColors(containerColor = Color(RED.toColorInt()))
                        } else {
                            IconButtonDefaults.iconButtonColors(containerColor = Color.White)
                        },
                        modifier = Modifier
                            .size(80.dp)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Pause or resume",
                            tint = if (isRunning) Color.White else Color.Black,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Finish button only showed when is not running
                AnimatedVisibility(
                    visible = !isRunning,
                    enter = slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth/2 }, // start from the right
                        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth/2 }, // slide out to the right
                        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
                    )
                ) {
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        TextButton(
                            modifier = Modifier
                                .size(80.dp),
                            onClick = onFinishClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(RED.toColorInt()))
                        ) {
                            Text(
                                text = "FINISH",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = modifier.height(120.dp)
    )

}