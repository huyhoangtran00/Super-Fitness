package com.example.superfitness.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.local.db.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    // Lấy tất cả người dùng
    fun getAllUsers(): LiveData<List<UserProfile>>  {

        val users = userProfileDao.getAllUsers()

        // Observe và log khi dữ liệu thay đổi
        users.observeForever { userList ->
            userList?.forEach { user ->
                Log.d("ROOM_DB", "User: ${user.id}, ${user.name}")
            }
        }
        return users

    }

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

    // Trả về BMI dưới dạng Float
    suspend fun getUserBmi(userId: Int): Float? = userProfileDao.getUserBmi(userId)

    // Trả về weight dưới dạng Float
    suspend fun getUserWeight(userId: Int): Float? = userProfileDao.getUserWeight(userId)
    fun hasUserProfile(): Flow<Boolean> = userProfileDao.getUserCount().map { it > 0 }

}
