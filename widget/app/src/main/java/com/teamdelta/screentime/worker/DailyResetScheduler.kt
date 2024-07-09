package com.teamdelta.screentime.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Object for scheduling the reset task for the daily timer.
 */
object DailyResetScheduler {
    private const val DAILY_RESET_WORK_NAME = "daily_reset_work"

    fun scheduleDailyResetAtMidnight(context: Context) {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Set up date at midnight
        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        dueDate.add(Calendar.DAY_OF_MONTH, 1)

        // Calculate initial delay
        val initialDelay = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyResetWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_RESET_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}