package com.example.superfitness

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.ui.screens.run.RunDestination
import com.example.superfitness.ui.screens.run.RunScreen
import com.example.superfitness.ui.screens.WaterTrackingApp
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.ui.screens.UserProfileInputScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {
    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val userProfileDao: UserProfileDao = db.userProfileDao()
        val userProfileRepository = UserProfileRepository(userProfileDao)
        val factory = UserProfileViewModelFactory(userProfileRepository)
        userProfileViewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)

        setContent {
            MaterialTheme {
                MainScreen(
                    userProfileViewModel,
                    openSettings = ::openAppSettings
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: UserProfileViewModel,
    openSettings: () -> Unit
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { CustomBottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "record",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("record") { UserProfileInputScreen(viewModel) }
            composable("water") { WaterTrackingApp() }
            composable(route = RunDestination.route) {
                RunScreen(
                    openSettings = openSettings,
                    modifier = Modifier.fillMaxSize()
            ) }
            composable("weather") { UserProfileInputScreen(viewModel) }
            composable("settings") { UserProfileInputScreen(viewModel) }


            // home thi chieu tien do record ->
            // bat dau -> muc tieu 5kmmm
            // uong nuoc
            // weather
            // cai dat
        }
    }
}

@Composable
fun CustomBottomNavigationBar(navController: NavHostController) {
    // Get the current back stack entry as State
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Fetch the current destination
    val currentDestination = navBackStackEntry?.destination
    val currentRoute  = currentDestination?.route
    NavigationBar { // Dùng NavigationBar của M3 thay cho Row
        // Trang chủ
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Trang chủ") },
            label = { Text("Trang chủ") },
            selected = currentRoute == "record",
            onClick = { navController.navigate("record") }
        )

        // Hoạt động
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
            onClick = {  navController.navigateSingleTopTo(route = RunDestination.route)}
        )


        NavigationBarItem(
            icon = { Icon(Icons.Filled.WbSunny, contentDescription = "Weather") },
            label = { Text("Weather") },
            selected = currentRoute == "weather",
            onClick = {  navController.navigate("weather")}
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Setting") },
            selected = currentRoute == "settings",
            onClick = {  navController.navigate("settings")}
        )
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun NavHostController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        // most only one copy of a destination on the top of the back stack
        launchSingleTop = true
        // pop up to the Start destination of the graph
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        restoreState = true
    }
}


