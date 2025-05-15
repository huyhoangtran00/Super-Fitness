package com.example.superfitness.utils
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_INTERVAL = intPreferencesKey("reminder_interval")
    }

    // Lưu trạng thái nhắc nhở
    suspend fun saveReminderSettings(enabled: Boolean, interval: Int) {
        dataStore.edit { preferences ->
            preferences[REMINDER_ENABLED] = enabled
            preferences[REMINDER_INTERVAL] = interval
        }
    }

    // Đọc trạng thái nhắc nhở
    val reminderSettings: Flow<Pair<Boolean, Int>> = dataStore.data
        .map { preferences ->
            Pair(
                preferences[REMINDER_ENABLED] ?: false,
                preferences[REMINDER_INTERVAL] ?: 60
            )
        }
}