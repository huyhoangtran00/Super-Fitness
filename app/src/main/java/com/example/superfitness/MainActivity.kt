package com.example.superfitness

import android.os.Bundle
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
import com.example.superfitness.ui.screens.WaterTrackingApp
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.ui.screens.UserProfileInputScreen

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
                MainScreen(userProfileViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: UserProfileViewModel) {
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
            composable("activity") { UserProfileInputScreen(viewModel) }
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
    // ... (phần khai báo navBackStackEntry giữ nguyên)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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
            selected = currentRoute == "activity",
            onClick = {  navController.navigate("activity")}
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
// Màn hình Trang chủ
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Trang chủ", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Số bước chân hôm nay: 5,000", style = MaterialTheme.typography.bodyLarge)
        Text("Thời tiết: 28°C, Nắng", style = MaterialTheme.typography.bodyLarge)
        Text("Nhắc nhở uống nước: Còn 1.5L", style = MaterialTheme.typography.bodyLarge)
    }
}

// Màn hình Hoạt động
@Composable
fun ActivityScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hoạt động thể chất", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Số bước chân: 5,000", style = MaterialTheme.typography.bodyLarge)
        Text("Chạy bộ: 3 km, 300 kcal", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { /* TODO: Mở danh sách bài tập */ }) {
            Text("Xem bài tập")
        }
    }
}

