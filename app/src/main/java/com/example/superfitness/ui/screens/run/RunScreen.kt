package com.example.superfitness.ui.screens.run

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.R
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.screens.run.components.RunningCard
import com.example.superfitness.ui.screens.run.components.StartScreenBody
import com.example.superfitness.ui.screens.run.components.StartScreenBottomBar
import com.example.superfitness.ui.screens.run.components.StartScreenTopBar
import com.example.superfitness.ui.screens.run.components.TrackMap
import com.example.superfitness.viewmodel.AppViewModelProvider
import com.example.superfitness.viewmodel.RunViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

object RunDestination : NavigationDestination {
    override val route = "run"
    override val titleRes = R.string.run
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RunScreen(
    viewModel: RunViewModel = viewModel(factory = AppViewModelProvider.Factory),
    openSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val isGpsAvailable = viewModel.isGpsAvailable.collectAsStateWithLifecycle().value
    val locationUiState = viewModel.locationUiState.collectAsStateWithLifecycle().value
    val isFirstRun = locationUiState.isFirstRun

    AnimatedVisibility(isFirstRun) {
        StartScreen(
            modifier = Modifier.fillMaxSize(),
            locationPermissions = locationPermissions,
            isGpsAvailable = isGpsAvailable,
            openSettings = openSettings,
            onStartClick = {
                performTrackingService(context, locationUiState.isTracking)
            }
        )
    }
    AnimatedVisibility(!isFirstRun) {
        TrackingScreen(
            modifier = Modifier.fillMaxSize(),
            locationUiState = locationUiState
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    locationPermissions: MultiplePermissionsState,
    isGpsAvailable: Boolean,
    openSettings: () -> Unit,
    onStartClick: () -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            StartScreenTopBar(
                scrollBehavior = scrollBehavior
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
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            )
        }
    ) { paddingValues ->
        StartScreenBody(
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

@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier,
    locationUiState: LocationUiState
) {
    val context = LocalContext.current
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
            modifier = Modifier
                .padding(16.dp),
            onPlayClicked =
                { performTrackingService(context, locationUiState.isTracking) },
            onFinishClicked = {

            },
            isTracking = locationUiState.isTracking,
            durationTimerInMillis = locationUiState.durationTimerInMillis,
            distanceInMeters = locationUiState.distanceInMeters,
            speedInKmH = locationUiState.speedInKmH
        )
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













