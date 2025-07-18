package com.example.superfitness.ui.screens.run.components

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
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.example.superfitness.ui.components.LocationPermissionTextProvider
import com.example.superfitness.ui.components.PermissionDialog
import com.example.superfitness.utils.GREEN
import com.example.superfitness.utils.RED
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

@Composable
fun StartScreenBody(
    modifier: Modifier = Modifier,
    arePermissionsGranted: Boolean,
    isGpsAvailable: Boolean,
    showDialog: Boolean,
    isPermanentlyDeclined: Boolean,
    cancelDialog: () -> Unit,
    launchRationale: () -> Unit,
    goToSystemSetting: () -> Unit
) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(36.73723, 3.08647), 3f)
    }

    var showMessage by rememberSaveable { mutableStateOf(false) }
    var showMap by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isGpsAvailable) {
        if (isGpsAvailable) {
            showMessage = true
            delay(3000)
            showMessage = false
        } else {
            showMessage = false
        }
    }

    // Delay map loading to wait for screen animation
    LaunchedEffect(Unit) {
        delay(600L) //
        showMap = true
    }


    if (arePermissionsGranted) {
        Box(
            modifier = modifier
        ) {
            if (showMap) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                )
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
            Text("Running function needs precise location permission")
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
