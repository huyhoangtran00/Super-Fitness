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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun UserProfileInputScreen(
    userProfileViewModel: UserProfileViewModel,
    onProfileSaved: () -> Unit = {}
) {
    var step by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf<Int?>(null) }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var selectedHeight by remember { mutableStateOf(170) }
    var selectedWeight by remember { mutableStateOf(60) }
    var selectedGoal by remember { mutableStateOf<String?>(null) }

    val ageOptions = (10..100).toList()
    val genderOptions = listOf("Nam", "Nữ", "Khác")
    val heightOptions = (140..210).toList()
    val weightOptions = (40..150).toList()
    val goalOptions = listOf("Giảm cân", "Duy trì cân nặng", "Tăng cơ")

    val isNameValid by derivedStateOf { name.length >= 2 }
    val isFormComplete by derivedStateOf {
        name.isNotBlank() && selectedAge != null && selectedGender != null &&
                selectedHeight != null && selectedWeight != null && selectedGoal != null
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Thông tin cá nhân",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    if (step > 0) {
                        IconButton(onClick = { step-- }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            AnimatedContent(targetState = step < 4) { visible ->
                if (visible) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Button(
                                onClick = { step++ },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = when (step) {
                                    0 -> isNameValid
                                    1 -> selectedAge != null && selectedGender != null
                                    2 -> true
                                    3 -> selectedWeight != null
                                    else -> true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = if (step == 3) "Hoàn thành" else "Tiếp tục",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                when (step) {
                    0 -> NameStep(name, isNameValid, { name = it })
                    1 -> DemographicStep(selectedAge, selectedGender, ageOptions, genderOptions,
                        { selectedAge = it }, { selectedGender = it })
                    2 -> HeightStep(selectedHeight, { selectedHeight = it })
                    3 -> WeightGoalStep(selectedWeight, selectedGoal, weightOptions, goalOptions,
                        { selectedWeight = it }, { selectedGoal = it })
                    4 -> ConfirmationStep(
                        name,
                        selectedAge,
                        selectedGender,
                        selectedHeight,
                        selectedWeight,
                        selectedGoal,
                        { userProfileViewModel.insertUser(
                            UserProfile(
                                name = name,
                                age = selectedAge ?: 0,
                                gender = selectedGender ?: "",
                                height = selectedHeight.toFloat(),
                                weight = selectedWeight.toFloat(),
                                goal = selectedGoal ?: "",
                                bmi = calculateBMI(selectedHeight, selectedWeight)
                            )
                        )
                            onProfileSaved()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NameStep(
    name: String,
    isNameValid: Boolean,
    onNameChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepHeader("Bước 1/4: Thông tin cơ bản")

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isNameValid && name.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        if (!isNameValid && name.isNotBlank()) {
            Text(
                "Tên cần ít nhất 2 ký tự",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun DemographicStep(
    selectedAge: Int?,
    selectedGender: String?,
    ageOptions: List<Int>,
    genderOptions: List<String>,
    onAgeSelect: (Int) -> Unit,
    onGenderSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        StepHeader("Bước 2/4: Nhân khẩu học")

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tuổi", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ageOptions.chunked(10).first()) { age ->
                    FilterChip(
                        selected = selectedAge == age,
                        onClick = { onAgeSelect(age) },
                        label = { Text("$age") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Giới tính", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                genderOptions.forEach { gender ->
                    FilterChip(
                        selected = selectedGender == gender,
                        onClick = { onGenderSelect(gender) },
                        label = { Text(gender) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeightStep(
    selectedHeight: Int,
    onHeightChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        StepHeader("Bước 3/4: Chiều cao")

        Slider(
            value = selectedHeight.toFloat(),
            onValueChange = { onHeightChange(it.toInt()) },
            valueRange = 140f..210f,
            steps = 69,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            "$selectedHeight cm",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun WeightGoalStep(
    selectedWeight: Int,
    selectedGoal: String?,
    weightOptions: List<Int>,
    goalOptions: List<String>,
    onWeightChange: (Int) -> Unit,
    onGoalSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        StepHeader("Bước 4/4: Cân nặng & Mục tiêu")

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Cân nặng (kg)", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = selectedWeight.toFloat(),
                onValueChange = { onWeightChange(it.toInt()) },
                valueRange = 40f..150f,
                steps = 109,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                "$selectedWeight kg",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Mục tiêu", style = MaterialTheme.typography.labelLarge)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                goalOptions.forEach { goal ->
                    ElevatedAssistChip(
                        onClick = { onGoalSelect(goal) },
                        label = { Text(goal) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedGoal == goal)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (selectedGoal == goal)
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        else
                            null
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmationStep(
    name: String,
    age: Int?,
    gender: String?,
    height: Int,
    weight: Int,
    goal: String?,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Xác nhận thông tin",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileInfoItem("Họ tên", name)
                ProfileInfoItem("Tuổi", age?.toString() ?: "")
                ProfileInfoItem("Giới tính", gender ?: "")
                ProfileInfoItem("Chiều cao", "$height cm")
                ProfileInfoItem("Cân nặng", "$weight kg")
                ProfileInfoItem("Mục tiêu", goal ?: "")
                ProfileInfoItem("BMI", String.format("%.1f", calculateBMI(height, weight)))
            }
        }

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Lưu thông tin", fontSize = 16.sp)
        }
    }
}

@Composable
private fun StepHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

private fun calculateBMI(height: Int, weight: Int): Float {
    val heightM = height.toFloat() / 100
    return weight.toFloat() / (heightM * heightM)
}