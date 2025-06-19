package com.example.dreamapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: false
    }

    val notificationHour: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATION_HOUR] ?: 21 // по умолчанию 21:00
    }

    val notificationMinute: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATION_MINUTE] ?: 0
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATION_HOUR] = hour
            prefs[NOTIFICATION_MINUTE] = minute
        }
    }
} 