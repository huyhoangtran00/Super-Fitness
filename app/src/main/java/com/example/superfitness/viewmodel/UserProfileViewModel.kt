package com.example.superfitness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.superfitness.data.local.db.entity.UserProfile
import com.example.superfitness.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


open class UserProfileViewModel (private val userProfileRepository: UserProfileRepository) : ViewModel() {

    // Lấy tất cả người dùng từ repository
    fun getAllUsers(): LiveData<List<UserProfile>> {
        return userProfileRepository.getAllUsers()
    }

    // Thêm người dùng mới vào repository
    fun insertUser(user: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            userProfileRepository.insertUser(user)
        }
    }

    // Lấy người dùng theo ID
    fun getUserById(userId: Int): LiveData<UserProfile?> {
        return liveData(Dispatchers.IO) {
            emit(userProfileRepository.getUserById(userId))
        }
    }

    // Xóa người dùng
    fun deleteUser(user: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            userProfileRepository.deleteUser(user)
        }
    }
}
