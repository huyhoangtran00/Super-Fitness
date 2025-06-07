package com.example.superfitness.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.utils.BLUE
import com.example.superfitness.utils.RED
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = viewModel()
) {
    val hasProfile by viewModel.hasUserProfile.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val userProfile by viewModel.getUserById(1).observeAsState(initial = null)

    var isEditing by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Form states
    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var goal by rememberSaveable { mutableStateOf("") }

    // Error states
    var ageError by rememberSaveable { mutableStateOf(false) }
    var heightError by rememberSaveable { mutableStateOf(false) }
    var weightError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name
            age = it.age.toString()
            gender = it.gender
            height = it.height.toString()
            weight = it.weight.toString()
            goal = it.goal
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ cá nhân", style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )) },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color(RED.toColorInt())
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isEditing) {
                ExtendedFloatingActionButton(
                    onClick = {
                        // Validate inputs
                        ageError = age.toIntOrNull() == null
                        heightError = height.toFloatOrNull() == null
                        weightError = weight.toFloatOrNull() == null

                        if (!ageError && !heightError && !weightError) {
                            viewModel.insertUser(
                                UserProfile(
                                    id = 1,
                                    name = name,
                                    age = age.toInt(),
                                    gender = gender,
                                    height = height.toFloat(),
                                    weight = weight.toFloat(),
                                    bmi = calculateBMI(height.toFloat(), weight.toFloat()),
                                    goal = goal
                                )
                            )
                            isEditing = false
                        }
                    },
                    icon = { Icon(Icons.Default.Save, "Lưu", tint = Color(RED.toColorInt())) },
                    text = { Text("LƯU THAY ĐỔI", color = Color(RED.toColorInt())) },
                    modifier = Modifier.padding(bottom = 16.dp),
                    containerColor = Color("#FFE5E0".toColorInt())
                )
            }
        }
    ) { padding ->
        when {
            isLoading -> FullScreenLoading()

            userProfile == null -> EmptyProfilePrompt { isEditing = true }

            else -> Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                AnimatedVisibility(visible = !isEditing) {
                    ProfileOverview(
                        name = name,
                        age = age,
                        gender = gender,
                        height = height,
                        weight = weight,
                        goal = goal,
                        bmi = calculateBMI(height.toFloatOrNull() ?: 0f, weight.toFloatOrNull() ?: 0f)
                    )
                }

                AnimatedVisibility(
                    visible = isEditing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ProfileEditForm(
                        name = name,
                        onNameChange = { name = it },
                        age = age,
                        onAgeChange = {
                            age = it
                            ageError = it.toIntOrNull() == null
                        },
                        ageError = ageError,
                        gender = gender,
                        onGenderChange = { gender = it },
                        height = height,
                        onHeightChange = {
                            height = it
                            heightError = it.toFloatOrNull() == null
                        },
                        heightError = heightError,
                        weight = weight,
                        onWeightChange = {
                            weight = it
                            weightError = it.toFloatOrNull() == null
                        },
                        weightError = weightError,
                        goal = goal,
                        onGoalChange = { goal = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileOverview(
    name: String,
    age: String,
    gender: String,
    height: String,
    weight: String,
    goal: String,
    bmi: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoItem(icon = Icons.Default.PermIdentity, title = "Họ và tên", value = name)
            InfoItem(icon = Icons.Default.Cake, title = "Tuổi", value = "${age} tuổi")
            InfoItem(icon = Icons.Default.SupervisedUserCircle, title = "Giới tính", value = gender)
            InfoItem(icon = Icons.Default.Height, title = "Chiều cao", value = "${height}cm")
            InfoItem(icon = Icons.Default.MonitorWeight, title = "Cân nặng", value = "${weight}kg")
            InfoItem(icon = Icons.Default.Flag, title = "Mục tiêu", value = goal)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BMI", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                FilterChip(
                    selected = false, // Không cần trạng thái chọn
                    onClick = {}, // Không cần xử lý click
                    label = { Text("%.1f".format(bmi)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = when {
                            bmi < 18.5 -> Color(0xFF81D4FA) // Màu xanh dương nhạt (Underweight)
                            bmi < 25 -> Color(0xFFA5D6A7)   // Màu xanh lá cây nhạt (Normal)
                            bmi < 30 -> Color(0xFFFFF59D)   // Màu vàng nhạt (Overweight)
                            else -> Color(0xFFEF9A9A)       // Màu đỏ nhạt (Obese)
                        },
                        labelColor = MaterialTheme.colorScheme.onSurface // Màu chữ
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileEditForm(
    name: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    ageError: Boolean,
    gender: String,
    onGenderChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    heightError: Boolean,
    weight: String,
    onWeightChange: (String) -> Unit,
    weightError: Boolean,
    goal: String,
    onGoalChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Họ và tên") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            isError = name.isBlank(),
            supportingText = { if(name.isBlank()) Text("Vui lòng nhập tên") },
            modifier = Modifier.fillMaxWidth()
        )

        NumberInputField(
            value = age,
            onValueChange = onAgeChange,
            label = "Tuổi",
            icon = Icons.Default.Cake,
            isError = ageError,
            errorMessage = "Tuổi không hợp lệ"
        )

        GenderSelection(gender = gender, onGenderSelected = onGenderChange)

        NumberInputField(
            value = height,
            onValueChange = onHeightChange,
            label = "Chiều cao (cm)",
            icon = Icons.Default.Height,
            isError = heightError,
            errorMessage = "Chiều cao không hợp lệ"
        )

        NumberInputField(
            value = weight,
            onValueChange = onWeightChange,
            label = "Cân nặng (kg)",
            icon = Icons.Default.MonitorWeight,
            isError = weightError,
            errorMessage = "Cân nặng không hợp lệ"
        )

        GoalSelection(selectedGoal = goal, onGoalSelected = onGoalChange)
    }
}

@Composable
private fun GenderSelection(gender: String, onGenderSelected: (String) -> Unit) {
    val genders = listOf("Nam", "Nữ", "Khác")

    Column {
        Text("Giới tính", style = MaterialTheme.typography.labelLarge)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            genders.forEach { g ->
                FilterChip(
                    selected = gender == g,
                    onClick = { onGenderSelected(g) },
                    label = { Text(g) },
                    leadingIcon = if (gender == g) { { Icon(Icons.Default.Check, null, tint = Color(RED.toColorInt())) } } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color("#FFE5E0".toColorInt()),
                        selectedLabelColor = Color(RED.toColorInt())
                    )
                )
            }
        }
    }
}

@Composable
private fun GoalSelection(selectedGoal: String, onGoalSelected: (String) -> Unit) {
    val goals = listOf("Giảm cân", "Duy trì", "Tăng cân")

    Column {
        Text("Mục tiêu", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(goals) { goal ->
                ElevatedAssistChip(
                    onClick = { onGoalSelected(goal) },
                    label = { Text(goal) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedGoal == goal)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isError: Boolean,
    errorMessage: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 5) onValueChange(it) },
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        supportingText = { if(isError) Text(errorMessage, color = MaterialTheme.colorScheme.error) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun InfoItem(modifier: Modifier = Modifier, icon: ImageVector, title: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, null, tint = Color(RED.toColorInt()))
        Spacer(Modifier.width(16.dp))
        Column(modifier = modifier) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = Color.Black)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = Color.Black, maxLines = 1)
        }
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyProfilePrompt(onCreate: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Chưa có hồ sơ. Tạo ngay!", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCreate) {
                Text("Tạo Hồ Sơ")
            }
        }
    }
}

/**
 * Tính BMI dựa trên chiều cao (cm) và cân nặng (kg).
 */
fun calculateBMI(heightCm: Float, weightKg: Float): Float {
    if (heightCm <= 0f) return 0f
    val heightM = heightCm / 100f
    val bmi = weightKg / (heightM * heightM)
    return (bmi * 10).roundToInt() / 10f
}