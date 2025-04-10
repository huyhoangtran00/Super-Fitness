package com.example.superfitness.ui.run

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.superfitness.R
import com.example.superfitness.ui.components.LocationPermissionTextProvider
import com.example.superfitness.ui.components.PermissionDialog
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.tracking.TrackingDestination
import com.example.superfitness.viewmodel.AppViewModelProvider
import com.example.superfitness.viewmodel.RunViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

object RunDestination : NavigationDestination {
    override val route = "run"
    override val titleRes = R.string.run
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RunScreen(
    viewModel: RunViewModel = viewModel(factory = AppViewModelProvider.Factory),
    locationPermissions: MultiplePermissionsState,
    navController: NavController,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier
) {

    val isGpsAvailable = viewModel.isGpsAvailable.collectAsStateWithLifecycle().value

    var showDialog by remember {
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            RunScreenTopBar(
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            RunScreenBottomBar(
                onClick = {
                    if (locationPermissions.allPermissionsGranted) { // Show the start running screen
                        navController.navigate(TrackingDestination.route)
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
        RunScreenBody(
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

    // Track if the permission is granted or not after the user interaction


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunScreenTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Run Screen",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color.Black
            )
        },
        navigationIcon = {
            TextButton(
                onClick = {}
            ) {
                Text(
                    text = "Close",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        },
        actions = {
            IconButton(
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Circle icon profile",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun RunScreenBody(
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

    var showMessage by remember { mutableStateOf(false) }

    LaunchedEffect(isGpsAvailable) {
        if (isGpsAvailable) {
            showMessage = true
            delay(3000)
            showMessage = false
        } else {
            showMessage = false
        }
    }


    if (arePermissionsGranted) {
        Box(
            modifier = modifier
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            )
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
                EditMessage("GPS is unavailable")
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
                EditMessage("GPS is available")
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

@Composable
fun RunScreenBottomBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    BottomAppBar(
        content = {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(80.dp),
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(text = "START", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun EditMessage(msg: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (msg == "GPS is unavailable") Color.Red else Color.Green,
        shadowElevation = 18.dp
    ) {
        Text(
            text = msg,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}













