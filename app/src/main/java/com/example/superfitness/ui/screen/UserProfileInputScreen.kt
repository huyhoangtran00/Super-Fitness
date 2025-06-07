package com.example.superfitness.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch
import kotlin.math.pow

private enum class ProfileStep { NAME, AGE, GENDER, HEIGHT, WEIGHT, GOAL, REVIEW }

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserProfileInputScreen(
    userProfileViewModel: UserProfileViewModel,
    onProfileSaved: () -> Unit = {}
) {
    var currentStep by remember { mutableStateOf(ProfileStep.NAME) }
    val profileState = remember { mutableStateOf(UserProfileState()) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ProfileProgressHeader(currentStep, Modifier.fillMaxWidth())
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(targetState = currentStep) { step ->
                when (step) {
                    ProfileStep.NAME -> NameStep(
                        state = profileState.value,
                        onNext = { if (it.name.isNotBlank()) currentStep = ProfileStep.AGE },
                        modifier = Modifier.fillMaxSize()
                    )
                    ProfileStep.AGE -> NumberStep(
                        title = "Select your age",
                        range = 10..100,
                        selectedValue = profileState.value.age,
                        onValueSelected = { profileState.value = profileState.value.copy(age = it) },
                        onNext = { if (profileState.value.age != null) currentStep = ProfileStep.GENDER },
                        onBack = { currentStep = ProfileStep.NAME },
                        unit = "years"
                    )
                    ProfileStep.GENDER -> OptionsStep(
                        title = "Select your gender",
                        options = listOf("Male", "Female", "Other"),
                        selectedValue = profileState.value.gender,
                        onValueSelected = { profileState.value = profileState.value.copy(gender = it) },
                        onNext = { if (profileState.value.gender != null) currentStep = ProfileStep.HEIGHT },
                        onBack = { currentStep = ProfileStep.AGE }
                    )
                    ProfileStep.HEIGHT -> NumberStep(
                        title = "Select your height",
                        range = 140..210,
                        selectedValue = profileState.value.height,
                        onValueSelected = { profileState.value = profileState.value.copy(height = it) },
                        onNext = { if (profileState.value.height != null) currentStep = ProfileStep.WEIGHT },
                        onBack = { currentStep = ProfileStep.GENDER },
                        unit = "cm"
                    )
                    ProfileStep.WEIGHT -> NumberStep(
                        title = "Select your weight",
                        range = 40..150,
                        selectedValue = profileState.value.weight,
                        onValueSelected = { profileState.value = profileState.value.copy(weight = it) },
                        onNext = { if (profileState.value.weight != null) currentStep = ProfileStep.GOAL },
                        onBack = { currentStep = ProfileStep.HEIGHT },
                        unit = "kg"
                    )
                    ProfileStep.GOAL -> OptionsStep(
                        title = "Select your goal",
                        options = listOf("Lose weight", "Maintain weight", "Gain muscle"),
                        selectedValue = profileState.value.goal,
                        onValueSelected = { profileState.value = profileState.value.copy(goal = it) },
                        onNext = { if (profileState.value.goal != null) currentStep = ProfileStep.REVIEW },
                        onBack = { currentStep = ProfileStep.WEIGHT }
                    )
                    ProfileStep.REVIEW -> ReviewStep(
                        state = profileState.value,
                        onSave = {
                            coroutineScope.launch {
                                try {
                                    userProfileViewModel.insertUser(profileState.value.toUserProfile())
                                    onProfileSaved()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error saving profile: ${e.message}")
                                }
                            }
                        },
                        onBack = { currentStep = ProfileStep.GOAL },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileProgressHeader(currentStep: ProfileStep, modifier: Modifier = Modifier) {
    val steps = ProfileStep.values()
    val progress = (steps.indexOf(currentStep) / (steps.size - 1f))

    LinearProgressIndicator(
        progress = progress,
        modifier = modifier.height(4.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun NameStep(
    state: UserProfileState,
    onNext: (UserProfileState) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(state.name) }

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Let's get started!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onNext(state.copy(name = name)) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = name.isNotBlank()
        ) {
            Text("Continue", fontSize = 16.sp)
        }
    }
}

@Composable
private fun NumberStep(
    title: String,
    range: IntRange,
    selectedValue: Int?,
    onValueSelected: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    unit: String
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        DropdownSelector(
            items = range.toList(),
            selectedItem = selectedValue,
            onItemSelected = onValueSelected,
            formatter = { "$it $unit" }
        )

        NavigationButtons(
            onBack = onBack,
            onNext = onNext,
            nextEnabled = selectedValue != null
        )
    }
}

// Thay thế ChoiceChip bằng FilterChip
@Composable
private fun OptionsStep(
    title: String,
    options: List<String>,
    selectedValue: String?,
    onValueSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        options.forEach { option ->
            FilterChip(
                selected = selectedValue == option,
                onClick = { onValueSelected(option) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(option) }
            )
        }

        NavigationButtons(
            onBack = onBack,
            onNext = onNext,
            nextEnabled = selectedValue != null
        )
    }
}
@Composable
private fun ReviewStep(
    state: UserProfileState,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Review your profile",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        ProfileDetailItem("Name", state.name)
        ProfileDetailItem("Age", state.age?.toString() ?: "")
        ProfileDetailItem("Gender", state.gender ?: "")
        ProfileDetailItem("Height", "${state.height} cm")
        ProfileDetailItem("Weight", "${state.weight} kg")
        ProfileDetailItem("Goal", state.goal ?: "")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text("Confirm and Save", fontSize = 16.sp)
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Edit")
        }
    }
}

@Composable
private fun ProfileDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

@Composable
private fun NavigationButtons(
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Button(
            onClick = onNext,
            enabled = nextEnabled
        ) {
            Text("Next")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownSelector(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    formatter: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedItem?.let(formatter) ?: "Please select",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(formatter(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

private data class UserProfileState(
    val name: String = "",
    val age: Int? = null,
    val gender: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val goal: String? = null
) {
    fun toUserProfile(): UserProfile {
        val heightValue = height?.toFloat() ?: 0f
        val weightValue = weight?.toFloat() ?: 0f
        val bmi = if (heightValue > 0) weightValue / ((heightValue / 100).pow(2)) else 0f

        return UserProfile(
            id = 0,
            name = name,
            age = age ?: 0,
            gender = gender ?: "",
            height = heightValue,
            weight = weightValue,
            bmi = bmi,
            goal = goal ?: ""
        )
    }
}