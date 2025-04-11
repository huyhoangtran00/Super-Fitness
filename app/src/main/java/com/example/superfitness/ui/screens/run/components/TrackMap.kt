package com.example.superfitness.ui.screens.run.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.superfitness.utils.MAP_ZOOM
import com.example.superfitness.utils.POLYLINE_WIDTH
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

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