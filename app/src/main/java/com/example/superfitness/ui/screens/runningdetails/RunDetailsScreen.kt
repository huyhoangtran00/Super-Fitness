package com.example.superfitness.ui.screens.runningdetails

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.modifier.ModifierLocalModifierNode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.superfitness.R
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.utils.DrawableConverter
import com.example.superfitness.utils.GREEN
import com.example.superfitness.utils.LocationsUtils
import com.example.superfitness.utils.POLYLINE_WIDTH
import com.example.superfitness.utils.RED
import com.example.superfitness.viewmodel.AppViewModelProvider
import com.example.superfitness.viewmodel.RunDetailsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

object RunDetailsDestination : NavigationDestination {
    override val route: String = "details"
    override val titleRes: Int = R.string.details
    const val runItemIdArg = "runItemId"
    val routeWithArgs = "$route/{$runItemIdArg}"
    val arguments = listOf(
        navArgument(RunDetailsDestination.runItemIdArg) {
            type = NavType.IntType
        }
    )
}

@Composable
fun RunDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: RunDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBackClick: () -> Unit
) {
//    val scope = rememberCoroutineScope()
//    val selectedRun = viewModel.runItem.collectAsStateWithLifecycle().value
//
//    val pathPoints = LocationsUtils.stringToPathPoints(selectedRun.pathPoints)
    val selectedRun = viewModel.runItem.collectAsStateWithLifecycle().value
    val pathPoints: List<LatLng> = LocationsUtils.stringToPathPoints(selectedRun.pathPoints)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DetailsTrackMap(
            pathPoints = pathPoints
        )

        TopBackClick(
            modifier = Modifier
                .align(Alignment.TopStart),
            onClick = onBackClick
        )
    }

}

@Composable
fun BoxScope.DetailsTrackMap(
    modifier: Modifier = Modifier,
    pathPoints: List<LatLng>,
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState()

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true
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
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 60)
                )
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
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

            Marker(
                state = rememberMarkerState(position = pathPoints.first()),
                anchor = Offset(0.5f, 0.5f),
                icon = DrawableConverter
                    .bitmapDescriptorFromVector(
                        LocalContext.current,
                        R.drawable.baseline_adjust_24,
                        tint = Color.Blue.toArgb(),
                        scale = 1.0
                    )
            )

            Marker(
                state = rememberMarkerState(position = pathPoints.last()),
                anchor = Offset(0.5f, 0.5f),
                icon = DrawableConverter
                    .bitmapDescriptorFromVector(
                        LocalContext.current,
                        R.drawable.baseline_flag_24,
                        tint = Color(GREEN.toColorInt()).toArgb(),
                        scale = 1.5
                    )
            )

        }
    }
}

@Composable
fun TopBackClick(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(color = Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null
            )
        }
    }
}