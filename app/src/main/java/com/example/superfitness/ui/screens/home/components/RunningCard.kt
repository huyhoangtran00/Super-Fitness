package com.example.superfitness.ui.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.superfitness.data.local.db.entity.RunEntity
import com.example.superfitness.utils.DistanceKmFormatter
import com.example.superfitness.utils.LocationsUtils
import com.example.superfitness.utils.POLYLINE_WIDTH
import com.example.superfitness.utils.RED
import com.example.superfitness.utils.TimeUtilFormatter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunningCard(
    modifier: Modifier = Modifier,
    runItem: RunEntity,
    onClick: () -> Unit
) {
    val dateAndTime = TimeUtilFormatter.getDate(runItem.timeStamp)
    val timeOfDay = TimeUtilFormatter.getTimeOfTheDay(runItem.timeStamp)
    val duration = TimeUtilFormatter.getTime(runItem.duration)
    val distance = DistanceKmFormatter.metersToKm(runItem.distance)
    val averagePace = DistanceKmFormatter.calculateAveragePace(runItem.duration, runItem.distance)
    val pathPoints = LocationsUtils.stringToPathPoints(runItem.pathPoints)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = onClick
            )
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
                Text(
                    text = dateAndTime,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.Gray,
                    )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
                Text(
                    text = "$timeOfDay RUN",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            RunningStatistics(
                distance = distance,
                averagePace = averagePace,
                duration = duration,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            MapImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                pathPoints = pathPoints,
                onClick = onClick
            )
        }
    }
}

@Composable
fun RunningStatistics(
    modifier: Modifier = Modifier,
    distance: String,
    averagePace: String,
    duration: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Distance",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$distance km",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(Modifier.width(32.dp))
        Column {
            Text(
                text = "Pace",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$averagePace/km",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(Modifier.width(32.dp))
        Column {
            Text(
                text = "Time",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = duration,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MapImage(
    modifier: Modifier = Modifier,
    pathPoints: List<LatLng>,
    onClick: () -> Unit
) {
    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = false
            )
        )
    }

    if (pathPoints.size > 1) {
        val bounds = remember {
            LatLngBounds.builder().apply {
                for(point in pathPoints) {
                    this.include(point)
                }
            }.build()
        }

        LaunchedEffect(key1 = isMapLoaded) {
            if (isMapLoaded) {
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngBounds(bounds, 16)
                )
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true }
        ) {
            if (pathPoints.size > 1) {
                Polyline(
                    points = pathPoints,
                    color = Color(RED.toColorInt()),
                    width = POLYLINE_WIDTH
                )
            }
        }
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .combinedClickable(onClick = onClick)
        ) {

        }
    }
}