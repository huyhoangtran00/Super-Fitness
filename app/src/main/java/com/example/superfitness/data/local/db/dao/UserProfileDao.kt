package com.example.superfitness.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.superfitness.data.local.db.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserProfile): Long

    @Update
    suspend fun update(user: UserProfile): Int

    @Delete
    suspend fun delete(user: UserProfile): Int

    @Query("SELECT * FROM user_profile WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserProfile?

    @Query("SELECT * FROM user_profile")
    fun getAllUsers(): LiveData<List<UserProfile>>

    @Query("SELECT COUNT(*) FROM user_profile")
    fun getUserCount(): Flow<Int>


    // Lấy BMI của người dùng theo ID (trả về Float)
    @Query("SELECT bmi FROM user_profile WHERE id = :userId")
    suspend fun getUserBmi(userId: Int): Float?

    // Lấy cân nặng của người dùng theo ID (trả về Float)
    @Query("SELECT weight FROM user_profile WHERE id = :userId")
    suspend fun getUserWeight(userId: Int): Float?
}
