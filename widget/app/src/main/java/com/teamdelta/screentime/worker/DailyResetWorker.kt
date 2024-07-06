package com.teamdelta.screentime.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.teamdelta.screentime.timer.DailyTimer
import java.util.concurrent.TimeUnit

class DailyResetWorker(context: Context, params:WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        DailyTimer.reset()
        return Result.success()
    }

    private fun scheduleResetTask() {
        // Schedule WorkManager task for daily reset
        val resetWorkRequest = PeriodicWorkRequestBuilder<DailyResetWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_reset",
            ExistingPeriodicWorkPolicy.KEEP,
            resetWorkRequest
        )
    }
}