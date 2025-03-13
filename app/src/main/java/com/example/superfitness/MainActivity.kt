package com.example.superfitness

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.ui.theme.SuperFitnessTheme
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.data.local.db.entity.UserProfile

class MainActivity : ComponentActivity() {

    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperFitnessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Khởi tạo AppDatabase và UserProfileRepository
        val db = AppDatabase.getDatabase(this)
        val userProfileDao: UserProfileDao = db.userProfileDao()
        val userProfileRepository = UserProfileRepository(userProfileDao)

        // Khởi tạo UserProfileViewModel thông qua ViewModelProvider và ViewModelFactory
        val factory = UserProfileViewModelFactory(userProfileRepository)
        userProfileViewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)

        testDatabase()
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
