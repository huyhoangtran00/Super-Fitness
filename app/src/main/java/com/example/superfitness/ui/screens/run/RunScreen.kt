package com.example.superfitness.ui.screens.run

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.R
import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.screens.run.components.StartScreenBody
import com.example.superfitness.ui.screens.run.components.StartScreenBottomBar
import com.example.superfitness.ui.screens.run.components.StartScreenTopBar
import com.example.superfitness.ui.screens.run.components.TrackScreenBody
import com.example.superfitness.ui.screens.run.components.TrackScreenBottomBar
import com.example.superfitness.ui.screens.run.components.TrackScreenTopBar
import com.example.superfitness.utils.LocationsUtils
import com.example.superfitness.data.di.AppViewModelProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay

object RunDestination : NavigationDestination {
    override val route = "run"
    override val titleRes = R.string.run
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RunScreen(
    viewModel: RunViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onCloseScreenClick: () -> Unit,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    )

    val isGpsAvailable by viewModel.isGpsAvailable.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val locationUiState by viewModel.locationUiState.collectAsStateWithLifecycle()
    val isFirstRun = locationUiState.isFirstRun

    val showTrackingScreen = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400L) // Delay to ensure transition completes
        showTrackingScreen.value = true
    }

    AnimatedVisibility(isFirstRun) {
        StartScreen(
            modifier = Modifier.fillMaxSize(),
            currentLocation = currentLocation,
            locationPermissions = locationPermissions,
            isGpsAvailable = isGpsAvailable,
            openSettings = openSettings,
            onStartClick = {
                performTrackingService(context, Actions.START_OR_RESUME)
            },
            onCloseScreenClick = onCloseScreenClick
        )
    }
    AnimatedVisibility(!isFirstRun) {
        TrackingScreen(
            modifier = Modifier.fillMaxSize(),
            locationUiState = locationUiState,
            onCloseScreenClick = onCloseScreenClick,
            saveRun = {
                viewModel.addRun(
                    RunEntity(
                        timeStamp = TrackingService.startTime,
                        distance = locationUiState.distanceInMeters,
                        duration = locationUiState.durationTimerInMillis,
                        pathPoints = LocationsUtils.pathPointsToString(locationUiState.pathPoints),
                        steps = locationUiState.steps
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    currentLocation: LatLng?,
    locationPermissions: MultiplePermissionsState,
    isGpsAvailable: Boolean,
    onCloseScreenClick: () -> Unit,
    openSettings: () -> Unit,
    onStartClick: () -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            StartScreenTopBar(
                modifier = Modifier.fillMaxWidth(),
                onCloseScreenClick = onCloseScreenClick
            )
        },
        bottomBar = {
            StartScreenBottomBar(
                onClick = {
                    if (locationPermissions.allPermissionsGranted) {
                        // Show the start running screen
                        // And start the service
                        onStartClick()
                    } else {
                        showDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    ) { paddingValues ->
        StartScreenBody(
            currentLocation = currentLocation,
            arePermissionsGranted = locationPermissions.allPermissionsGranted,
            isGpsAvailable = isGpsAvailable,
            showDialog = showDialog,
            isPermanentlyDeclined = locationPermissions.shouldShowRationale,
            cancelDialog = { showDialog = false },
            launchRationale = { locationPermissions.launchMultiplePermissionRequest() },
            goToSystemSetting = openSettings,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier,
    locationUiState: LocationUiState,
    onCloseScreenClick: () -> Unit,
    saveRun: () -> Unit
) {
    val context = LocalContext.current

    var showSaverDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TrackScreenTopBar(
                isRunning = locationUiState.isTracking,
                onCloseScreenClick = onCloseScreenClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            TrackScreenBottomBar(
                modifier = Modifier.fillMaxWidth(),
                isRunning = locationUiState.isTracking,
                onPauseAndResumeClick = {
                    if(locationUiState.isTracking) {
                        performTrackingService(context, Actions.PAUSE)
                    } else {
                        performTrackingService(context, Actions.START_OR_RESUME)
                    }
                },
                onFinishClick = {
                    showSaverDialog = true
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        TrackScreenBody(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            isRunning = locationUiState.isTracking,
            showSaverDialog = showSaverDialog,
            currentLocation = locationUiState.currentLocation,
            pathPoints = locationUiState.pathPoints,
            durationTimerInMillis = locationUiState.durationTimerInMillis,
            distanceInMeters = locationUiState.distanceInMeters,
            speedInKmH = locationUiState.speedInKmH,
            bearing = locationUiState.bearing,
            steps = locationUiState.steps,
            onDismissDialog = { showSaverDialog = false },
            onStopAndSaveClick = {
                saveRun()
                showSaverDialog = false
                performTrackingService(context, Actions.STOP)
                onCloseScreenClick()
            },
            onStopAndNotSaveClick = {
                showSaverDialog = false
                performTrackingService(context, Actions.STOP)
                onCloseScreenClick()
            }
        )

    }
}
private fun performTrackingService(
    context: Context,
    actions: Actions
) {
    // Create an Explicit Intent with explicit service you want to interact
    Intent(context, TrackingService::class.java).also {
        it.action = actions.name
        context.startService(it) // Send intent to start that specific action in service
    }
}

@Preview
@Composable
fun TrackScreenPreview() {
    TrackingScreen(
        locationUiState = LocationUiState(),
        onCloseScreenClick = {},
        saveRun = {}
    )
}













