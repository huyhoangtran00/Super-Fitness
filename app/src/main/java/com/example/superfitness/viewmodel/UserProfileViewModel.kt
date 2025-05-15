package com.example.superfitness.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserProfileViewModel (
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _hasUserProfile = MutableStateFlow(false)
    val hasUserProfile: StateFlow<Boolean> = _hasUserProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkUserProfile()
    }

    fun checkUserProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _hasUserProfile.value = userProfileRepository.hasUserProfile().first()
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error checking user profile", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun insertUser(user: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userProfileRepository.insertUser(user)
                // Cập nhật lại hasUserProfile sau khi lưu thành công
                checkUserProfile()
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error inserting user", e)
            }
        }
    }

    fun getAllUsers(): LiveData<List<UserProfile>> {
        return userProfileRepository.getAllUsers()
    }

    fun getUserById(userId: Int): LiveData<UserProfile?> {
        return liveData(Dispatchers.IO) {
            emit(userProfileRepository.getUserById(userId))
        }
    }

    fun deleteUser(user: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userProfileRepository.deleteUser(user)
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error deleting user", e)
            }
        }
    }
    fun getUserBmiLive(userId: Int): LiveData<Float?> = liveData(Dispatchers.IO) {
        emit(userProfileRepository.getUserBmi(userId))
    }

    fun getUserWeightLive(userId: Int): LiveData<Float?> = liveData(Dispatchers.IO) {
        emit(userProfileRepository.getUserWeight(userId))
    }

    /**
     * Trả về lượng nước cần uống dựa theo BMI và cân nặng.
     */
    suspend fun getWaterTargetValue(userId: Int): Float{
    val bmi = userProfileRepository.getUserBmi(userId) ?: 0f
        val weight = userProfileRepository.getUserWeight(userId) ?: 0f
        return calculateWaterTarget(weight, bmi)
    }

    /**
     * Hàm tính toán lượng nước cần uống (ml)
     */
    private fun calculateWaterTarget(weightKg: Float, bmi: Float): Float {
        val factor = when {
            bmi < 18.5f -> 40f
            bmi < 25f   -> 35f
            else        -> 30f
        }
        return weightKg * factor
    }
}