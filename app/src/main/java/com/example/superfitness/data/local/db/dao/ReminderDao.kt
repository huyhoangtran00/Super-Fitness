package com.example.superfitness.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.superfitness.data.local.db.entity.Reminder

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder) : Long

    @Update
    suspend fun update(reminder: Reminder) : Int

    @Delete
    suspend fun delete(reminder: Reminder) : Int

    @Query("SELECT * FROM reminder WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: Int): Reminder?

    @Query("SELECT * FROM reminder")
    fun getAllReminders(): LiveData<List<Reminder>>
}