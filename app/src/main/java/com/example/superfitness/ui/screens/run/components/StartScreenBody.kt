package com.example.superfitness.ui.screens.run.components

import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.superfitness.R
import com.example.superfitness.ui.components.LocationPermissionTextProvider
import com.example.superfitness.ui.components.PermissionDialog
import com.example.superfitness.utils.DrawableConverter
import com.example.superfitness.utils.GREEN
import com.example.superfitness.utils.MAP_ZOOM
import com.example.superfitness.utils.RED
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StartScreenBody(
    modifier: Modifier = Modifier,
    currentLocation: LatLng?,
    arePermissionsGranted: Boolean,
    isGpsAvailable: Boolean,
    showDialog: Boolean,
    isPermanentlyDeclined: Boolean,
    cancelDialog: () -> Unit,
    launchRationale: () -> Unit,
    goToSystemSetting: () -> Unit,
) {

    val cameraPositionState = rememberCameraPositionState()

    var showMessage by rememberSaveable { mutableStateOf(false) }
    var showMap by rememberSaveable { mutableStateOf(false) }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
            ),
        )
    }

    LaunchedEffect(isGpsAvailable) {
        if (isGpsAvailable) {
            showMessage = true
            delay(3000)
            showMessage = false
        } else {
            showMessage = false
        }
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(currentLocation, MAP_ZOOM)
                )
            )
        }
    }

    // Delay map loading to wait for screen animation
    LaunchedEffect(Unit) {
        delay(900L) //
        showMap = true
    }


    if (arePermissionsGranted) {
        Box(
            modifier = modifier
        ) {
            if (showMap) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    uiSettings = mapUiSettings,
                    cameraPositionState = cameraPositionState
                ) {
                    currentLocation?.let {
                        val currentMarkerState = rememberMarkerState().apply {
                            position = currentLocation
                        }
                        Marker(
                            state = currentMarkerState,
                            anchor = Offset(0.5f, 0.5f),
                            flat = true
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }

            AnimatedVisibility(
                visible = !isGpsAvailable,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
                )
            ) {
                EditMessage("GPS is unavailable", Color(RED.toColorInt()))
            }
            AnimatedVisibility(
                visible = showMessage,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
                )
            ) {
                EditMessage("GPS is available", Color(GREEN.toColorInt()))
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Running function needs \nprecise location permission",
                textAlign = TextAlign.Center
            )
            if (showDialog) {
                PermissionDialog(
                    permissionTextProvider = LocationPermissionTextProvider(),
                    isPermanentlyDeclined = isPermanentlyDeclined,
                    onDismiss = { cancelDialog() },
                    onOkClick = {
                        cancelDialog()
                        launchRationale()
                    },
                    onGoToSystemSettingsClick = { goToSystemSetting() }
                )
            }
        }
    }
}
