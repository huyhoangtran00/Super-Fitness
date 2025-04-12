package com.example.superfitness.ui.tracking


import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.R
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.utils.MAP_ZOOM
import com.example.superfitness.utils.POLYLINE_WIDTH
import com.example.superfitness.utils.TimeUtilFormatter
import com.example.superfitness.viewmodel.AppViewModelProvider
import com.example.superfitness.viewmodel.ShareViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay

object TrackingDestination : NavigationDestination {
    override val route = "tracking"
    override val titleRes = R.string.tracking
}

@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier,
    viewModel: ShareViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current 

    val locationUiState by viewModel.locationUiState.collectAsStateWithLifecycle()

    var isShowRunningCard by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        delay(300L)
        isShowRunningCard = true
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        TrackMap(
            currentLocation = locationUiState.currentLocation,
            pathPoints = locationUiState.pathPoints
        )
        RunningCard(
            locationUiState = locationUiState,
            onPlayClicked =
                {performTrackingService(context, locationUiState.isTracking)},
            onFinishClicked = {

            },
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Composable
fun BoxScope.TrackMap(
    modifier: Modifier = Modifier,
    currentLocation: LatLng,
    pathPoints: List<LatLng>
) {
    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
            ),
        )
    }

    LoadingCircularProgress(isMapLoaded)

    LaunchedEffect(key1 = currentLocation) {
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(currentLocation, MAP_ZOOM)
            )
        )
    }

    GoogleMap(
        uiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        onMapLoaded = { isMapLoaded = true},
        modifier = Modifier.fillMaxSize()
    ) {
        val currentMarkerState = rememberMarkerState()
        currentMarkerState.position = currentLocation

        Marker(
            state = currentMarkerState,
            anchor = Offset(0.5f, 0.5f)
        )

        if (pathPoints.size > 1) {
            Polyline(
                points = pathPoints,
                color = Color.Green,
                width = POLYLINE_WIDTH
            )
        }
    }
}

@Composable
fun BoxScope.LoadingCircularProgress(
    isMapLoaded: Boolean = false
) {
    AnimatedVisibility(
        visible = isMapLoaded,
        enter = EnterTransition.None,
        exit = fadeOut(),
        modifier = Modifier.matchParentSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize()
        )
    }
}

@Composable
fun RunningCard(
    modifier: Modifier = Modifier,
    locationUiState: LocationUiState,
    onPlayClicked: () -> Unit,
    onFinishClicked: () -> Unit,
) {
    val formattedTime = TimeUtilFormatter.getFormattedStopWatchTime(locationUiState.durationTimerInMillis)
    ElevatedCard(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        RunningCardTime(
            durationTimer = formattedTime,
            isTracking = locationUiState.isTracking,
            onPlayClicked = onPlayClicked,
            onFinishedClicked = onFinishClicked,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 4.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            RunningStatusItem(
                painter = painterResource(R.drawable.ic_action_name),
                unit = "km",
                value = String.format("%.2f", locationUiState.distanceInMeters / 1000f),
                modifier = modifier,
            )

            RunningStatusItem(
                painter = painterResource(R.drawable.ic_action_name),
                unit = "km/h",
                value = String.format("%.2f", locationUiState.speedInKmH),
                modifier = modifier,
            )
        }
    }
}

@Composable
fun RunningCardTime(
    modifier: Modifier = Modifier,
    durationTimer: String,
    isTracking: Boolean,
    onPlayClicked: () -> Unit,
    onFinishedClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Running time",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = durationTimer,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        IconButton(
            onClick = onPlayClicked,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (isTracking) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                ),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

//        AnimatedVisibility(isTracking) {
//            IconButton(
//                onClick = onFinishedClicked,
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(
//                        color = MaterialTheme.colorScheme.secondary,
//                        shape = MaterialTheme.shapes.medium)
//                    .align(Alignment.CenterVertically)
//            ) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(
//                        id = R.drawable.baseline_flag_24
//                    ),
//                    contentDescription = null,
//                    modifier = Modifier.size(16.dp),
//                    tint = MaterialTheme.colorScheme.onSecondary
//                )
//            }
//        }
    }
}

@Composable
fun RunningStatusItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    unit: String,
    value: String
) {
    Row(modifier = Modifier.padding(horizontal = 4.dp)) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(top =  4.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun performTrackingService(
    context: Context,
    isTracking: Boolean
) {
    Intent(context, TrackingService::class.java).also {
        if (!isTracking) {
            it.action = Actions.START_OR_RESUME.name
            context.startService(it)
        } else {
            it.action = Actions.PAUSE.name
            context.startService(it)
        }
    }
}