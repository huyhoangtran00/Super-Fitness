package com.example.superfitness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.superfitness.repository.ReminderRepository
import com.example.superfitness.data.local.db.entity.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(private val reminderRepository: ReminderRepository) : ViewModel() {

    // Lấy tất cả nhắc nhở từ repository
    fun getAllReminders(): LiveData<List<Reminder>> {
        return reminderRepository.getAllReminders()
    }

    // Thêm nhắc nhở vào repository
    fun insertReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.insertReminder(reminder)
        }
    }

    // Lấy nhắc nhở theo ID
    fun getReminderById(reminderId: Int): LiveData<Reminder?> {
        return liveData(Dispatchers.IO) {
            emit(reminderRepository.getReminderById(reminderId))
        }
    }

    // Xóa nhắc nhở
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.deleteReminder(reminder)
        }
    }
}
