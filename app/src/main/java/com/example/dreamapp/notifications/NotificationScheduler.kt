package com.example.dreamapp.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val WORK_NAME = "dream_reminder_work"

    fun scheduleDailyNotification(context: Context, hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        val targetTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute))
        val initialDelay = if (now.isBefore(targetTime)) {
            Duration.between(now, targetTime)
        } else {
            Duration.between(now, targetTime.plusDays(1))
        }

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay.toMinutes(), TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelNotification(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
} 