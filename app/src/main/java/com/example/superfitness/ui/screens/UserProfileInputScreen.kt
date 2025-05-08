package com.example.superfitness.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.viewmodel.UserProfileViewModel
import kotlinx.coroutines.delay


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileInputScreen(
    userProfileViewModel: UserProfileViewModel,
    onProfileSaved: () -> Unit = {}
) {
    // State management
    var step by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf<Int?>(null) }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var selectedHeight by remember { mutableStateOf<Int?>(null) }
    var selectedWeight by remember { mutableStateOf<Int?>(null) }
    var selectedGoal by remember { mutableStateOf<String?>(null) }

    // Data options
    val ageOptions = (10..100).toList()
    val genderOptions = listOf("Nam", "Nữ", "Khác")
    val heightOptions = (140..210).toList()
    val weightOptions = (40..150).toList()
    val goalOptions = listOf("Giảm cân", "Duy trì cân nặng", "Tăng cơ")

    // Validation flags
    val isNameValid by derivedStateOf { name.length >= 2 }
    val isFormComplete by derivedStateOf {
        name.isNotBlank() && selectedAge != null && selectedGender != null &&
                selectedHeight != null && selectedWeight != null && selectedGoal != null
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thông tin cá nhân") },
                navigationIcon = {
                    if (step > 0) {
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (step < 4) {
                Button(
                    onClick = { step++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = when (step) {
                        0 -> isNameValid
                        1 -> selectedAge != null && selectedGender != null
                        2 -> selectedHeight != null
                        3 -> selectedWeight != null
                        else -> true
                    }
                ) {
                    Text(if (step == 3) "Hoàn thành" else "Tiếp tục")
                }
            }

            if (step == 5){





            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (step) {
                // Step 1: Tên
                0 -> {
                    Text("Bước 1/4: Thông tin cơ bản", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = !isNameValid && name.isNotBlank()
                    )
                    if (!isNameValid && name.isNotBlank()) {
                        Text("Tên cần ít nhất 2 ký tự", color = MaterialTheme.colorScheme.error)
                    }
                }

                // Step 2: Tuổi & Giới tính
                1 -> {
                    Text("Bước 2/4: Nhân khẩu học", style = MaterialTheme.typography.titleMedium)

                    // Tuổi
                    Text("Tuổi", style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ageOptions.chunked(10).first()) { age ->
                            FilterChip(
                                selected = selectedAge == age,
                                onClick = { selectedAge = age },
                                label = { Text("$age") }
                            )
                        }
                    }

                    // Giới tính
                    Text("Giới tính", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        genderOptions.forEach { gender ->
                            FilterChip(
                                selected = selectedGender == gender,
                                onClick = { selectedGender = gender },
                                label = { Text(gender) }
                            )
                        }
                    }
                }

                // Step 3: Chiều cao
                2 -> {
                    Text("Bước 3/4: Chiều cao", style = MaterialTheme.typography.titleMedium)
                    Slider(
                        value = selectedHeight?.toFloat() ?: 170f,
                        onValueChange = { selectedHeight = it.toInt() },
                        valueRange = heightOptions.first().toFloat()..heightOptions.last().toFloat(),
                        steps = heightOptions.size - 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "${selectedHeight ?: 170} cm",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Step 4: Cân nặng & Mục tiêu
                3 -> {
                    Text("Bước 4/4: Cân nặng & Mục tiêu", style = MaterialTheme.typography.titleMedium)

                    // Cân nặng
                    Text("Cân nặng (kg)", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = selectedWeight?.toFloat() ?: 60f,
                        onValueChange = { selectedWeight = it.toInt() },
                        valueRange = weightOptions.first().toFloat()..weightOptions.last().toFloat(),
                        steps = weightOptions.size - 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "${selectedWeight ?: 60} kg",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Mục tiêu
                    Text("Mục tiêu", style = MaterialTheme.typography.labelLarge)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        goalOptions.forEach { goal ->
                            ElevatedAssistChip(
                                onClick = { selectedGoal = goal },
                                label = { Text(goal) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (selectedGoal == goal) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }

                // Step 5: Xác nhận
                4 -> {
                    val bmi: Float = if (selectedHeight != null && selectedWeight != null && selectedHeight!! > 0) {
                        val heightInMeters = selectedHeight!!.toFloat() / 100  // Chuyển từ cm sang mét
                        selectedWeight!!.toFloat() / (heightInMeters * heightInMeters)
                    } else {
                        0f
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Xác nhận thông tin", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Hiển thị thông tin đã nhập
                        ProfileInfoItem("Họ tên", name)
                        ProfileInfoItem("Tuổi", selectedAge?.toString() ?: "")
                        ProfileInfoItem("Giới tính", selectedGender ?: "")
                        ProfileInfoItem("Chiều cao", "${selectedHeight} cm")
                        ProfileInfoItem("Cân nặng", "${selectedWeight} kg")
                        ProfileInfoItem("Mục tiêu", selectedGoal ?: "")

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                userProfileViewModel.insertUser(
                                    UserProfile(
                                        name = name,
                                        age = selectedAge ?: 0,
                                        gender = selectedGender ?: "",
                                        height = selectedHeight!!.toFloat(),
                                        weight = selectedWeight!!.toFloat(),
                                        goal = selectedGoal ?: "",
                                        bmi = bmi,
                                    )
                                )
                                onProfileSaved() // Gọi callback khi lưu thành công
                            },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Lưu thông tin")
                        }



                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

