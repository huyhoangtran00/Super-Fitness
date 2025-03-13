package com.example.superfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.ui.viewmodel.UserProfileViewModel

class UserProfileViewModelFactory(private val userProfileRepository: UserProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            return UserProfileViewModel(userProfileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}