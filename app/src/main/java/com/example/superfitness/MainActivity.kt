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
    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.Icon
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.Settings
    import androidx.compose.material.icons.filled.WbSunny
    import androidx.compose.material.icons.filled.LocalDrink
    import androidx.compose.material.icons.filled.DirectionsRun
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.ViewModelProvider
    import androidx.navigation.NavHostController
    import androidx.navigation.compose.NavHost
    import androidx.navigation.compose.composable
    import androidx.navigation.compose.currentBackStackEntryAsState
    import androidx.navigation.compose.rememberNavController
    import com.example.superfitness.data.local.db.AppDatabase
    import com.example.superfitness.data.local.db.dao.UserProfileDao
    import com.example.superfitness.data.repository.UserProfileRepository
    import com.example.superfitness.ui.run.RunDestination
    import com.example.superfitness.ui.run.RunScreen
    import com.example.superfitness.ui.screens.WaterTrackingApp
    import com.example.superfitness.ui.viewmodel.UserProfileViewModel
    import com.example.superfitness.viewmodel.UserProfileViewModelFactory
    import com.example.superfitness.ui.screens.UserProfileInputScreen
    import com.example.superfitness.ui.tracking.TrackingDestination
    import com.example.superfitness.ui.tracking.TrackingScreen
    import com.google.accompanist.permissions.ExperimentalPermissionsApi
    import com.google.accompanist.permissions.rememberMultiplePermissionsState
    import androidx.hilt.navigation.compose.hiltViewModel
    import com.example.superfitness.data.local.db.dao.WaterIntakeDao
    import com.example.superfitness.repository.WaterIntakeRepository
    import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel
    import com.example.superfitness.viewmodel.WaterIntakeViewModelFactory
    import kotlinx.coroutines.flow.first

    class MainActivity : ComponentActivity() {
        private lateinit var userProfileViewModel: UserProfileViewModel
        private lateinit var waterIntakeViewModel: WaterIntakeViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()

            val db = AppDatabase.getDatabase(this)
            val userProfileDao: UserProfileDao = db.userProfileDao()
            val userProfileRepository = UserProfileRepository(userProfileDao)
            val user_factory = UserProfileViewModelFactory(userProfileRepository)
            userProfileViewModel = ViewModelProvider(this, user_factory).get(UserProfileViewModel::class.java)

            val waterIntakeDao: WaterIntakeDao = db.waterIntakeDao()
            val waterIntakeRepository = WaterIntakeRepository(waterIntakeDao)
            val water_factory = WaterIntakeViewModelFactory(waterIntakeRepository)
            waterIntakeViewModel = ViewModelProvider(this, water_factory).get(WaterIntakeViewModel::class.java)

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

    @Composable
    fun AppContent(
        userProfileViewModel: UserProfileViewModel,
        waterIntakeViewModel: WaterIntakeViewModel,
        openSettings: () -> Unit
    ) {
        val hasUserProfile by userProfileViewModel.hasUserProfile.collectAsState()
        val isLoading by userProfileViewModel.isLoading.collectAsState()

        val navController = rememberNavController()
        var shouldNavigateToMain by remember { mutableStateOf(false) }

        LaunchedEffect(shouldNavigateToMain) {
            if (shouldNavigateToMain) {
                navController.navigate("record") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
                shouldNavigateToMain = false
            }
        }

        when {
            isLoading -> {
                // Bạn có thể thay thế bằng animation hoặc màn loading đẹp hơn
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            hasUserProfile -> {
                MainScreen(
                    waterIntakeViewModel = waterIntakeViewModel,
                    openSettings = openSettings,
                    navController = navController
                )
            }
            else -> {
                UserProfileInputScreen(
                    userProfileViewModel = userProfileViewModel,
                    onProfileSaved = {
                        shouldNavigateToMain = true
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MainScreen(
        waterIntakeViewModel: WaterIntakeViewModel,
        openSettings: () -> Unit,
        navController: NavHostController,  // Thêm navController làm parameter

        startDestination: String = "record"
    ) {
        val locationPermissions = rememberMultiplePermissionsState(
            permissions = listOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )


        Scaffold(
            bottomBar = { CustomBottomNavigationBar(navController) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("record") { TrackingScreen(modifier = Modifier.fillMaxSize()) }
                composable("water") { WaterTrackingApp(waterIntakeViewModel) }
                composable(route = RunDestination.route) {
                    RunScreen(
                        locationPermissions = locationPermissions,
                        navController = navController,
                        openSettings = openSettings,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                composable(route = TrackingDestination.route) {
                    TrackingScreen(
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                composable("weather") { TrackingScreen(modifier = Modifier.fillMaxSize()) }
                composable("settings") { TrackingScreen(modifier = Modifier.fillMaxSize()) }
            }
        }
    }

    // Các composable khác giữ nguyên như cũ
    @Composable
    fun CustomBottomNavigationBar(navController: NavHostController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Trang chủ") },
                label = { Text("Trang chủ") },
                selected = currentRoute == "record",
                onClick = { navController.navigate("record") }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Filled.LocalDrink, contentDescription = "Water Reminder") },
                label = { Text("Drink Water") },
                selected = currentRoute == "water",
                onClick = { navController.navigate("water")}
            )

            NavigationBarItem(
                icon = { Icon(Icons.Filled.DirectionsRun, contentDescription = "Activity") },
                label = { Text("Activity") },
                selected = currentRoute == RunDestination.route,
                onClick = { navController.navigate(route = RunDestination.route)}
            )

            NavigationBarItem(
                icon = { Icon(Icons.Filled.WbSunny, contentDescription = "Weather") },
                label = { Text("Weather") },
                selected = currentRoute == "weather",
                onClick = { navController.navigate("weather")}
            )

            NavigationBarItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                label = { Text("Setting") },
                selected = currentRoute == "settings",
                onClick = { navController.navigate("settings")}
            )
        }
    }

    fun Activity.openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }