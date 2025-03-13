package com.example.superfitness.repository

import androidx.lifecycle.LiveData
import com.example.superfitness.data.local.db.dao.ReminderDao
import com.example.superfitness.data.local.db.entity.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {

    // Lấy tất cả nhắc nhở
    fun getAllReminders(): LiveData<List<Reminder>> = reminderDao.getAllReminders()

    // Thêm nhắc nhở
    suspend fun insertReminder(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    // Lấy nhắc nhở theo ID
    suspend fun getReminderById(reminderId: Int): Reminder? {
        return reminderDao.getReminderById(reminderId)
    }

    // Xóa nhắc nhở
    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.delete(reminder)
    }
}
