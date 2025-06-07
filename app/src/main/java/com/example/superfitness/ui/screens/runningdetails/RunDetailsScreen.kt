package com.example.superfitness.ui.screens.runningdetails

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.superfitness.R
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.screens.runningdetails.components.BottomSheetContent
import com.example.superfitness.ui.screens.runningdetails.components.TopBackClick
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
        navArgument(runItemIdArg) {
            type = NavType.IntType
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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

    val bottomScaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = bottomScaffoldState,
        sheetContent = {
            BottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                runItem = selectedRun,
                onDeleteClick = viewModel::deleteRun,
                onBackClick = onBackClick
            )
        },
        sheetContainerColor = Color.White,
        sheetPeekHeight = 90.dp,
        sheetSwipeEnabled = true,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 24.dp,
            topEnd = 24.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            DetailsTrackMap(
                modifier = Modifier.matchParentSize(),
                pathPoints = pathPoints,
                bottomScaffoldState = bottomScaffoldState,
            )
            TopBackClick(
                modifier = modifier,
                onClick = onBackClick
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTrackMap(
    modifier: Modifier = Modifier,
    pathPoints: List<LatLng>,
    bottomScaffoldState: BottomSheetScaffoldState
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

        LaunchedEffect(key1 = isMapLoaded, key2 = bottomScaffoldState.bottomSheetState.currentValue) {
            if (isMapLoaded) {
                when (bottomScaffoldState.bottomSheetState.currentValue) {
                    SheetValue.Expanded -> {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        )
                        cameraPositionState.animate(
                            CameraUpdateFactory.zoomOut()
                        )
                    }

                    SheetValue.PartiallyExpanded, SheetValue.Hidden -> {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        )
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
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
        AnimatedVisibility(bottomScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            Box(
                modifier = Modifier.fillMaxWidth().height(96.dp)
            )
        }
    }

}