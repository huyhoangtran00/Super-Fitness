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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.example.superfitness.ui.charts.BarChart
import com.example.superfitness.ui.charts.RunData
import com.example.superfitness.ui.charts.WaterData
import com.example.superfitness.ui.screens.SettingScreen
import com.example.superfitness.ui.screens.run.RunDestination
import com.example.superfitness.ui.screens.run.RunScreen
import com.example.superfitness.ui.screens.WaterTrackingApp
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.ui.screens.UserProfileInputScreen
import com.example.superfitness.ui.viewmodel.StepRecordViewModel
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel
import com.example.superfitness.viewmodel.StepRecordViewModelFactory
import com.example.superfitness.viewmodel.WaterIntakeViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var stepRecordViewModel: StepRecordViewModel
    private lateinit var waterIntakeViewModel: WaterIntakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val userProfileDao: UserProfileDao = db.userProfileDao()
        val userProfileRepository = UserProfileRepository(userProfileDao)
        val factory = UserProfileViewModelFactory(userProfileRepository)
        userProfileViewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)

        val stepRecordDao: StepRecordDao = db.stepRecordDao()
        val stepRecordRepository = StepRecordRepository(stepRecordDao)
        val stepRecordFactory = StepRecordViewModelFactory(stepRecordRepository)
        stepRecordViewModel = ViewModelProvider(this, stepRecordFactory).get(StepRecordViewModel::class.java)

        val waterIntakeDao: WaterIntakeDao = db.waterIntakeDao()
        val waterIntakeRepository = WaterIntakeRepository(waterIntakeDao)
        val waterIntakeFactory = WaterIntakeViewModelFactory(waterIntakeRepository)
        waterIntakeViewModel = ViewModelProvider(this, waterIntakeFactory).get(WaterIntakeViewModel::class.java)

        var isDataLoaded by mutableStateOf(false)

        lifecycleScope.launch {
            val existingStepRecords = stepRecordViewModel.getStepRecordByDate("2025-04-07")
            if (existingStepRecords == null) {
                stepRecordViewModel.insertStepRecord(StepRecord(id = 0, date = "2025-04-07", steps = 0, distance = 0f, calories = 0f, duration = "00:00:00"))
                stepRecordViewModel.insertStepRecord(StepRecord(id = 0, date = "2025-04-08", steps = 1312, distance = 1f, calories = 50f, duration = "00:20:02"))
                stepRecordViewModel.insertStepRecord(StepRecord(id = 0, date = "2025-04-09", steps = 0, distance = 0f, calories = 0f, duration = "00:00:00"))
                stepRecordViewModel.insertStepRecord(StepRecord(id = 0, date = "2025-04-10", steps = 9186, distance = 7f, calories = 350f, duration = "00:48:09"))
                stepRecordViewModel.insertStepRecord(StepRecord(id = 0, date = "2025-04-11", steps = 0, distance = 0f, calories = 0f, duration = "00:00:00"))
                stepRecordViewModel.insertStepRecord(StepRecord(id = 0, date = "2025-04-12", steps = 0, distance = 0f, calories = 0f, duration = "00:00:00"))
                stepRecordViewModel.insertStepRecord(StepRecord(id = 0, date = "2025-04-13", steps = 0, distance = 0f, calories = 0f, duration = "00:00:00"))
            }

            val existingIntakes = waterIntakeViewModel.getIntakesByDate("2025-04-07")
            if (existingIntakes == null) {
                waterIntakeViewModel.addIntake(amount = 500, type = "Water", customDate = "2025-04-07")
                waterIntakeViewModel.addIntake(amount = 1000, type = "Juice", customDate = "2025-04-08")
                waterIntakeViewModel.addIntake(amount = 0, type = "Water", customDate = "2025-04-09")
                waterIntakeViewModel.addIntake(amount = 750, type = "Water", customDate = "2025-04-10")
                waterIntakeViewModel.addIntake(amount = 0, type = "Water", customDate = "2025-04-11")
                waterIntakeViewModel.addIntake(amount = 0, type = "Water", customDate = "2025-04-12")
                waterIntakeViewModel.addIntake(amount = 2000, type = "Water", customDate = "2025-04-13")
            }
            delay(100)
            isDataLoaded = true
        }
        setContent {
            MaterialTheme {
                MainScreen(
                    userProfileViewModel = userProfileViewModel,
                    stepRecordViewModel = stepRecordViewModel,
                    waterIntakeViewModel = waterIntakeViewModel,
                    openSettings = ::openAppSettings
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    userProfileViewModel: UserProfileViewModel,
    stepRecordViewModel: StepRecordViewModel,
    waterIntakeViewModel: WaterIntakeViewModel,
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
            composable("record") { SettingScreen(
                stepRecordViewModel = stepRecordViewModel,
                waterIntakeViewModel = waterIntakeViewModel
            ) }
            composable("water") { WaterTrackingApp() }
            composable(route = RunDestination.route) {
                RunScreen(
                    openSettings = openSettings,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("weather") { UserProfileInputScreen(userProfileViewModel) }
            composable("settings") { UserProfileInputScreen(userProfileViewModel) }


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

