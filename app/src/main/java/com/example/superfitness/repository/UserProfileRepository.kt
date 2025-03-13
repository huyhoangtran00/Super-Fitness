package com.example.superfitness.data.repository

import androidx.lifecycle.LiveData
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.local.db.entity.UserProfile

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    // Lấy tất cả người dùng
    fun getAllUsers(): LiveData<List<UserProfile>> = userProfileDao.getAllUsers()

    // Thêm người dùng
    suspend fun insertUser(user: UserProfile) {
        userProfileDao.insert(user)
    }

    // Xóa người dùng
    suspend fun deleteUser(user: UserProfile) {
        userProfileDao.delete(user)
    }

    // Lấy người dùng theo ID
    suspend fun getUserById(userId: Int): UserProfile? {
        return userProfileDao.getUserById(userId)
    }
}
