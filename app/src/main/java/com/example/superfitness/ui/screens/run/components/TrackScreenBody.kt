package com.example.superfitness.ui.screens.run.components

import android.app.Dialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.superfitness.utils.DistanceKmFormatter
import com.example.superfitness.utils.RED
import com.example.superfitness.utils.SpeedFormatter
import com.example.superfitness.utils.TimeUtilFormatter
import com.google.android.gms.maps.model.LatLng

@Composable
fun TrackScreenBody(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    showSaverDialog: Boolean,
    currentLocation: LatLng,
    pathPoints: List<LatLng>,
    durationTimerInMillis: Long,
    distanceInMeters: Int,
    speedInKmH: Float,
    onDismissDialog: () -> Unit,
    onStopAndSaveClick: () -> Unit,
    onStopAndNotSaveClick: () -> Unit
) {

    val formattedTime = TimeUtilFormatter.getFormattedStopWatchTime(durationTimerInMillis)
    val formattedDistance = DistanceKmFormatter.metersToKm(distanceInMeters)
    val formattedSpeed = SpeedFormatter.getFormattedSpeedKmH(speedInKmH)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AnimatedVisibility(
                visible = !isRunning,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = LinearEasing)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = LinearEasing)
                ) + fadeOut()
            ) {
                EditMessage("Stopped", Color(RED.toColorInt()))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "TIME",
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            TrackMap(
                currentLocation = currentLocation,
                pathPoints = pathPoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Surface(
                modifier = Modifier
                    .height(196.dp),
                color = Color.White
            ) {
                TrackingStatistics(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    formattedSpeed = formattedSpeed,
                    formattedDistance = formattedDistance
                )
            }
        }
        if (showSaverDialog) {
            SaverDialog(
                onDismiss = onDismissDialog,
                onStopAndNotSaveClick = onStopAndNotSaveClick,
                onStopAndSaveClick = onStopAndSaveClick
            )
        }
    }
}

@Composable
fun TrackingStatistics(
    modifier: Modifier = Modifier,
    formattedSpeed: String,
    formattedDistance: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("SPEED(Km/h)", style = MaterialTheme.typography.labelSmall)
            Text(
                text = formattedSpeed,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            HorizontalDivider(modifier = Modifier.width(120.dp))
            Text("PACE", style = MaterialTheme.typography.labelSmall)
            Text(
                text = "0",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        VerticalDivider(
            modifier = Modifier.fillMaxHeight()
        )
        Column(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "DISTANCE",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = formattedDistance,
                style = MaterialTheme.typography.headlineMedium
                    .copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Km",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}