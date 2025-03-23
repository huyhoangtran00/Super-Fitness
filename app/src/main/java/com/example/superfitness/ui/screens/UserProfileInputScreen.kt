package com.example.superfitness.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.ui.viewmodel.UserProfileViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserProfileInputScreen(
    userProfileViewModel: UserProfileViewModel,
    onProfileSaved: () -> Unit = {}
) {
    // State variables cho từng thông tin
    var step by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf<Int?>(null) }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var selectedHeight by remember { mutableStateOf<Int?>(null) }
    var selectedWeight by remember { mutableStateOf<Int?>(null) }
    var selectedGoal by remember { mutableStateOf<String?>(null) }

    // Data cho Dropdowns
    val ageOptions = (10..100).toList()
    val genderOptions = listOf("Male", "Female", "Other")
    val heightOptions = (140..210).toList() // chiều cao tính bằng cm
    val weightOptions = (40..150).toList()  // cân nặng tính bằng kg
    val goalOptions = listOf("Lose weight", "Maintain weight", "Gain muscle")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(targetState = step) { currentStep ->
                when (currentStep) {
                    0 -> {
                        // Bước 0: Nhập tên
                        Text("Enter your name", style = MaterialTheme.typography.headlineMedium)
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { if (name.isNotBlank()) step++ }) {
                            Text("Next")
                        }
                    }
                    1 -> {
                        // Bước 1: Chọn tuổi
                        Text("Select your age", style = MaterialTheme.typography.headlineMedium)
                        var expanded by remember { mutableStateOf(false) }
                        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = selectedAge?.toString() ?: "Choose age")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            ageOptions.forEach { ageOption ->
                                DropdownMenuItem(
                                text = {Text(ageOption.toString())},
                                    onClick = {
                                        selectedAge = ageOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { step-- }) { Text("Back") }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { if (selectedAge != null) step++ }) { Text("Next") }
                        }
                    }
                    2 -> {
                        // Bước 2: Chọn giới tính
                        Text("Select your gender", style = MaterialTheme.typography.headlineMedium)
                        var expanded by remember { mutableStateOf(false) }
                        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = selectedGender ?: "Choose gender")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            genderOptions.forEach { genderOption ->
                                DropdownMenuItem(
                                    text = {Text(genderOption)},

                                    onClick = {
                                        selectedGender = genderOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { step-- }) { Text("Back") }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { if (!selectedGender.isNullOrBlank()) step++ }) { Text("Next") }
                        }
                    }
                    3 -> {
                        // Bước 3: Chọn chiều cao
                        Text("Select your height (cm)", style = MaterialTheme.typography.headlineMedium)
                        var expanded by remember { mutableStateOf(false) }
                        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = selectedHeight?.toString() ?: "Choose height")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            heightOptions.forEach { heightOption ->
                                DropdownMenuItem(
                                    text ={Text(heightOption.toString())},

                                    onClick = {
                                        selectedHeight = heightOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { step-- }) { Text("Back") }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { if (selectedHeight != null) step++ }) { Text("Next") }
                        }
                    }
                    4 -> {
                        // Bước 4: Chọn cân nặng
                        Text("Select your weight (kg)", style = MaterialTheme.typography.headlineMedium)
                        var expanded by remember { mutableStateOf(false) }
                        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = selectedWeight?.toString() ?: "Choose weight")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            weightOptions.forEach { weightOption ->
                                DropdownMenuItem(
                                    text = {Text(weightOption.toString())}
,
                                            onClick = {
                                        selectedWeight = weightOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { step-- }) { Text("Back") }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { if (selectedWeight != null) step++ }) { Text("Next") }
                        }
                    }
                    5 -> {
                        // Bước 5: Chọn mục tiêu sức khỏe
                        Text("Select your health goal", style = MaterialTheme.typography.headlineMedium)
                        var expanded by remember { mutableStateOf(false) }
                        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = selectedGoal ?: "Choose goal")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            goalOptions.forEach { goalOption ->
                                DropdownMenuItem(
                                    text = {Text(goalOption.toString())}

                                        ,
                                    onClick = {
                                        selectedGoal = goalOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { step-- }) { Text("Back") }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { if (!selectedGoal.isNullOrBlank()) step++ }) { Text("Next") }
                        }
                    }
                    6 -> {
                        // Bước cuối: Xem lại thông tin và lưu vào DB
                        Text("Review your information:", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Name: $name")
                        Text("Age: ${selectedAge ?: ""}")
                        Text("Gender: ${selectedGender ?: ""}")
                        Text("Height: ${selectedHeight ?: ""} cm")
                        Text("Weight: ${selectedWeight ?: ""} kg")
                        Text("Goal: ${selectedGoal ?: ""}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val parsedHeight = (selectedHeight ?: 0).toFloat()
                                val parsedWeight = (selectedWeight ?: 0).toFloat()
                                val bmi = if (parsedHeight > 0) {
                                    parsedWeight / ((parsedHeight / 100) * (parsedHeight / 100))
                                } else 0f
                                val userProfile = UserProfile(
                                    id = 0,  // Auto-generated ID
                                    name = name,
                                    age = selectedAge ?: 0,
                                    gender = selectedGender ?: "",
                                    height = parsedHeight,
                                    weight = parsedWeight,
                                    bmi = bmi,
                                    goal = selectedGoal ?: ""
                                )
                                userProfileViewModel.insertUser(userProfile)
                                onProfileSaved()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Profile", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { step-- }) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }
}
