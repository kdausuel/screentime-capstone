package com.teamdelta.screentime.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.teamdelta.screentime.timer.DailyTimer
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker for performing daily resets.
 *
 * This worker is responsible for resetting the daily timer at the scheduled time (midnight).
 */
class DailyResetWorker(context: Context, params:WorkerParameters) : CoroutineWorker(context, params){

    /**
     * Performs the work of resetting the daily timer.
     *
     * @return Result.success() if the work completed successfully, Result.retry() if the work should be retried,
     *         or Result.failure() if there was a permanent error.
     */
    override suspend fun doWork(): Result {
        DailyTimer.reset()
        return Result.success()
    }
}
