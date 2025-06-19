package com.example.dreamapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreamapp.data.SettingsRepository
import com.example.dreamapp.notifications.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    private val appContext = application.applicationContext

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val notificationHour: StateFlow<Int> = repository.notificationHour
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 21)
    val notificationMinute: StateFlow<Int> = repository.notificationMinute
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Следим за изменениями настроек и планируем уведомления
        viewModelScope.launch {
            combine(notificationsEnabled, notificationHour, notificationMinute) { enabled, hour, minute ->
                Triple(enabled, hour, minute)
            }.collect { (enabled, hour, minute) ->
                if (enabled) {
                    NotificationScheduler.scheduleDailyNotification(appContext, hour, minute)
                } else {
                    NotificationScheduler.cancelNotification(appContext)
                }
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.setNotificationTime(hour, minute)
        }
    }
} 