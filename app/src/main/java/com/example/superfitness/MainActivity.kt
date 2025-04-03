package com.example.superfitness

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.ui.theme.SuperFitnessTheme
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.ui.screen.UserProfileInputScreen
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val weatherViewModel by viewModels<WeatherViewModel>()
    private val userProfileViewModel by viewModels<UserProfileViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            weatherViewModel.loadWeatherInfo()
            weatherViewModel.loadForecastWeatherInfo()
        }
        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ))
        setContent {
            MainScreen(userProfileViewModel, weatherViewModel)
        }

//        // Khởi tạo AppDatabase và UserProfileRepository
//        val db = AppDatabase.getDatabase(this)
//        val userProfileDao: UserProfileDao = db.userProfileDao()
//        val userProfileRepository = UserProfileRepository(userProfileDao)
//
//        // Khởi tạo UserProfileViewModel thông qua ViewModelProvider và ViewModelFactory
//        val factory = UserProfileViewModelFactory(userProfileRepository)
//        userProfileViewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)
//
//        testDatabase()
    }

    private fun testDatabase() {
        // Thêm người dùng mới vào cơ sở dữ liệu
        val newUser = UserProfile(
            id = 1,
            name = "John Doe",
            age = 30,
            gender = "Male",
            height = 175f,
            weight = 70f,
            bmi = 22.9f,
            goal = "Increase strength"
        )

        // Thêm người dùng vào cơ sở dữ liệu
        userProfileViewModel.insertUser(newUser)

        // Kiểm tra và hiển thị tất cả người dùng từ cơ sở dữ liệu
        userProfileViewModel.getAllUsers().observe(this, Observer { users ->
            // Hiển thị danh sách người dùng
            users.forEach {
                Toast.makeText(this, "User: ${it.name}, Goal: ${it.goal}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

@Composable
fun MainScreen(viewModel: UserProfileViewModel, weatherViewModel: WeatherViewModel) {
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
            composable("weather") {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        WeatherCard(
                            state = weatherViewModel.state,
                            forecastState = weatherViewModel.stateForecastWeather
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
// Trang chủ
        Column(
            modifier = Modifier
                .clickable {
                    navController.navigate("weather") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(R.drawable.cloudy),
                modifier = Modifier.size(30.dp),
                contentDescription = "Weather",
                tint = if (currentRoute == "weather") Color.Yellow else Color.White
            )
            Text(
                text = "Weather",
                color = if (currentRoute == "weather") Color.Yellow else Color.White,
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
//@Composable
//fun CustomBottomNavigationBar(navController: NavHostController) {
//    // ... (phần khai báo navBackStackEntry giữ nguyên)
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//    NavigationBar { // Dùng NavigationBar của M3 thay cho Row
//        // Trang chủ
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.Home, contentDescription = "Trang chủ") },
//            label = { Text("Trang chủ") },
//            selected = currentRoute == "record",
//            onClick = { navController.navigate("record") }
//        )
//
//        // Hoạt động
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.LocalDrink, contentDescription = "Water Reminder") },
//            label = { Text("Drink Water") },
//            selected = currentRoute == "water",
//            onClick = { navController.navigate("water")}
//        )
//
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.DirectionsRun, contentDescription = "Activity") },
//            label = { Text("Activity") },
//            selected = currentRoute == "activity",
//            onClick = {  navController.navigate("activity")}
//        )
//
//
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.WbSunny, contentDescription = "Weather") },
//            label = { Text("Weather") },
//            selected = currentRoute == "weather",
//            onClick = {  navController.navigate("weather")}
//        )
//
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
//            label = { Text("Setting") },
//            selected = currentRoute == "settings",
//            onClick = {  navController.navigate("settings")}
//        )
//    }
//}
//// Màn hình Trang chủ
//@Composable
//fun HomeScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Trang chủ", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("Số bước chân hôm nay: 5,000", style = MaterialTheme.typography.bodyLarge)
//        Text("Thời tiết: 28°C, Nắng", style = MaterialTheme.typography.bodyLarge)
//        Text("Nhắc nhở uống nước: Còn 1.5L", style = MaterialTheme.typography.bodyLarge)
//    }
//}

//// Màn hình Hoạt động
//@Composable
//fun ActivityScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Hoạt động thể chất", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("Số bước chân: 5,000", style = MaterialTheme.typography.bodyLarge)
//        Text("Chạy bộ: 3 km, 300 kcal", style = MaterialTheme.typography.bodyLarge)
//        Button(onClick = { /* TODO: Mở danh sách bài tập */ }) {
//            Text("Xem bài tập")
//        }
//    }
//}

