package com.example.superfitness

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.compose.rememberNavController
import com.example.superfitness.ui.theme.SuperFitnessTheme
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.ui.WeatherCard
import com.example.superfitness.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.local.db.dao.WaterIntakeDao
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.repository.WaterIntakeRepository
import com.example.superfitness.ui.run.RunDestination
import com.example.superfitness.ui.run.RunScreen
import com.example.superfitness.ui.screens.UserProfileInputScreen
import com.example.superfitness.ui.screens.WaterTrackingApp
import com.example.superfitness.ui.tracking.TrackingDestination
import com.example.superfitness.ui.tracking.TrackingScreen
import com.example.superfitness.ui.theme.SuperFitnessTheme
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.viewmodel.WaterIntakeViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

import com.example.superfitness.ui.screen.UserProfileInputScreen
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var waterIntakeViewModel: WaterIntakeViewModel

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val weatherViewModel by viewModels<WeatherViewModel>()
    private val userProfileViewModel by viewModels<UserProfileViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val userProfileDao: UserProfileDao = db.userProfileDao()
        val userProfileRepository = UserProfileRepository(userProfileDao)
        val userFactory = UserProfileViewModelFactory(userProfileRepository)
        userProfileViewModel = ViewModelProvider(this, userFactory).get(UserProfileViewModel::class.java)

        val waterIntakeDao: WaterIntakeDao = db.waterIntakeDao()
        val waterIntakeRepository = WaterIntakeRepository(waterIntakeDao)
        val waterFactory = WaterIntakeViewModelFactory(waterIntakeRepository)
        waterIntakeViewModel = ViewModelProvider(this, waterFactory).get(WaterIntakeViewModel::class.java)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            weatherViewModel.loadWeatherInfo()
            weatherViewModel.loadForecastWeatherInfo()
            weatherViewModel.loadAirQualityInfo()
        }
        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ))
        setContent {
            MaterialTheme {
                AppContent(
                    userProfileViewModel = userProfileViewModel,
                    waterIntakeViewModel = waterIntakeViewModel,
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
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        WeatherCard(
                            state = weatherViewModel.state,
                            forecastState = weatherViewModel.stateForecastWeather,
                            airQualityState = weatherViewModel.airQualityState
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                    if(weatherViewModel.state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    weatherViewModel.state.error?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            composable("settings") {
                Box(modifier = Modifier.fillMaxSize())
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