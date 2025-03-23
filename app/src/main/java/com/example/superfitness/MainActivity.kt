package com.example.superfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.ui.theme.SuperFitnessTheme
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
            SuperFitnessTheme {
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
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen() }
            composable("activity") { ActivityScreen() }
            composable("water") { UserProfileInputScreen(viewModel) }
            composable("weather") {  }
            composable("setting") { ActivityScreen() }

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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trang chủ
        Column(
            modifier = Modifier
                .clickable {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Trang chủ",
                tint = if (currentRoute == "home") Color.Yellow else Color.White
            )
            Text(
                text = "Trang chủ",
                color = if (currentRoute == "home") Color.Yellow else Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Hoạt động
        Column(
            modifier = Modifier
                .clickable {
                    navController.navigate("activity") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Hoạt động",
                tint = if (currentRoute == "activity") Color.Yellow else Color.White
            )
            Text(
                text = "Hoạt động",
                color = if (currentRoute == "activity") Color.Yellow else Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Hồ sơ
        Column(
            modifier = Modifier
                .clickable {
                    navController.navigate("profile") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Hồ sơ",
                tint = if (currentRoute == "profile") Color.Yellow else Color.White
            )
            Text(
                text = "Hồ sơ",
                color = if (currentRoute == "profile") Color.Yellow else Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
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

// Màn hình Hồ sơ (Tích hợp từ UserProfileInputScreen)
@Composable
fun UserProfileInputScreen(viewModel: UserProfileViewModel) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hồ sơ sức khỏe", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và tên") }
        )
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Tuổi") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Giới tính") }
        )
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Chiều cao (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Cân nặng (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Mục tiêu sức khỏe") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val bmi = weight.toFloatOrNull()?.let { w ->
                height.toFloatOrNull()?.let { h -> w / ((h / 100) * (h / 100)) }
            } ?: 0f
            viewModel.insertUser(
                com.example.superfitness.data.local.db.entity.UserProfile(
                    id = 0, // Room sẽ tự động sinh ID
                    name = name,
                    age = age.toIntOrNull() ?: 0,
                    gender = gender,
                    height = height.toFloatOrNull() ?: 0f,
                    weight = weight.toFloatOrNull() ?: 0f,
                    bmi = bmi,
                    goal = goal
                )
            )
        }) {
            Text("Lưu hồ sơ")
        }
    }
}