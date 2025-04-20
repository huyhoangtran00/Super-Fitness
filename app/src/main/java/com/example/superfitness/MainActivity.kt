package com.example.superfitness

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.local.db.dao.StepRecordDao
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.local.db.dao.WaterIntakeDao
import com.example.superfitness.data.local.db.entity.StepRecord
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.repository.StepRecordRepository
import com.example.superfitness.repository.WaterIntakeRepository
import com.example.superfitness.ui.run.RunDestination
import com.example.superfitness.ui.run.RunScreen
import com.example.superfitness.ui.screens.SettingScreen
import com.example.superfitness.ui.screens.UserProfileInputScreen
import com.example.superfitness.ui.screens.WaterTrackingApp
import com.example.superfitness.ui.tracking.TrackingDestination
import com.example.superfitness.ui.tracking.TrackingScreen
import com.example.superfitness.ui.viewmodel.StepRecordViewModel
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.viewmodel.WaterIntakeViewModelFactory
import com.example.superfitness.viewmodel.StepRecordViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var waterIntakeViewModel: WaterIntakeViewModel
    private lateinit var stepRecordViewModel: StepRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val userProfileDao: UserProfileDao = db.userProfileDao()
        val userProfileRepository = UserProfileRepository(userProfileDao)
        val userFactory = UserProfileViewModelFactory(userProfileRepository)
        userProfileViewModel = ViewModelProvider(this, userFactory).get(UserProfileViewModel::class.java)

        val stepRecordDao: StepRecordDao = db.stepRecordDao()
        val stepRecordRepository = StepRecordRepository(stepRecordDao)
        val stepRecordFactory = StepRecordViewModelFactory(stepRecordRepository)
        stepRecordViewModel = ViewModelProvider(this, stepRecordFactory).get(StepRecordViewModel::class.java)

        val waterIntakeDao: WaterIntakeDao = db.waterIntakeDao()
        val waterIntakeRepository = WaterIntakeRepository(waterIntakeDao)
        val waterFactory = WaterIntakeViewModelFactory(waterIntakeRepository)
        waterIntakeViewModel = ViewModelProvider(this, waterFactory).get(WaterIntakeViewModel::class.java)

        setContent {
            MaterialTheme {
                AppContent(
                    userProfileViewModel = userProfileViewModel,
                    waterIntakeViewModel = waterIntakeViewModel,
                    stepRecordViewModel = stepRecordViewModel,
                    openSettings = ::openAppSettings
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppContent(
    userProfileViewModel: UserProfileViewModel,
    stepRecordViewModel: StepRecordViewModel,
    waterIntakeViewModel: WaterIntakeViewModel,
    openSettings: () -> Unit
) {
    val navController = rememberNavController()
    val hasUserProfile by userProfileViewModel.hasUserProfile.collectAsState()
    val isLoading by userProfileViewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(hasUserProfile, isLoading) {
        if (!isLoading && hasUserProfile) {
            navController.navigate("record") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (hasUserProfile) {
                CustomBottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (hasUserProfile) "record" else "profile_input",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("profile_input") {
                UserProfileInputScreen(
                    userProfileViewModel = userProfileViewModel,
                    onProfileSaved = {
                        navController.navigate("record") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("record") {
                TrackingScreen(modifier = Modifier.fillMaxSize())
            }
            composable("water") {
                WaterTrackingApp(waterIntakeViewModel)
            }
            composable(RunDestination.route) {
                RunScreen(
                    locationPermissions = rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    ),
                    navController = navController,
                    openSettings = openSettings,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("weather") {
                Box(modifier = Modifier.fillMaxSize())
            }
            composable("settings") {
                SettingScreen(
                    stepRecordViewModel = stepRecordViewModel,
                    waterIntakeViewModel = waterIntakeViewModel
                )
            }
        }
    }
}
@Composable
fun CustomBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
            label = { Text("Trang chủ") },
            selected = currentRoute == "record",
            onClick = { navController.navigate("record") { launchSingleTop = true } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocalDrink, contentDescription = "Nước") },
            label = { Text("Nước") },
            selected = currentRoute == "water",
            onClick = { navController.navigate("water") { launchSingleTop = true } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DirectionsRun, contentDescription = "Chạy") },
            label = { Text("Chạy") },
            selected = currentRoute == RunDestination.route,
            onClick = { navController.navigate(RunDestination.route) { launchSingleTop = true } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.WbSunny, contentDescription = "Thời tiết") },
            label = { Text("Thời tiết") },
            selected = currentRoute == "weather",
            onClick = { navController.navigate("weather") { launchSingleTop = true } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Cài đặt") },
            label = { Text("Cài đặt") },
            selected = currentRoute == "settings",
            onClick = { navController.navigate("settings") { launchSingleTop = true } }
        )
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}