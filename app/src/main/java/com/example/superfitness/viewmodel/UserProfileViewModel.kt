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

class UserProfileViewModel @Inject constructor(
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
}