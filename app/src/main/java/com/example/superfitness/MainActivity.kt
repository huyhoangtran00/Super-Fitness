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
import com.example.superfitness.ui.theme.SuperFitnessTheme
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.ui.WeatherCard
import com.example.superfitness.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var userProfileViewModel: UserProfileViewModel

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val weatherViewModel by viewModels<WeatherViewModel>()

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
            SuperFitnessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
            }
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SuperFitnessTheme {
        Greeting("Android")
    }
}
