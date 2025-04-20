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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toColorInt
import com.example.superfitness.R
import com.example.superfitness.utils.DrawableConverter
import com.example.superfitness.utils.GREEN
import com.example.superfitness.utils.MAP_ZOOM
import com.example.superfitness.utils.POLYLINE_WIDTH
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun TrackMap(
    modifier: Modifier = Modifier,
    bearing: Float,
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
        onMapLoaded = { isMapLoaded = true },
        modifier = modifier
    ) {
        val currentMarkerState = rememberMarkerState()
        currentMarkerState.position = currentLocation

        Marker(
            state = currentMarkerState,
            anchor = Offset(0.5f, 0.5f),
            rotation = bearing,
            flat = true,
            icon = DrawableConverter.bitmapDescriptorFromVector(
                LocalContext.current,
                R.drawable.arrow,
                scale = 1.0
            )
        )
        if (pathPoints.size > 1) {
            Polyline(
                points = pathPoints,
                color = Color(GREEN.toColorInt()),
                width = POLYLINE_WIDTH
            )
        }
    }
}