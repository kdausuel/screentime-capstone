package com.teamdelta.screentime.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.teamdelta.screentime.data.DataManager.setAlarmSetFlag
import com.teamdelta.screentime.timer.SessionTimer

/**
 * BroadcastReceiver for handling session reset events.
 *
 * This receiver is responsible for resetting the session timer and managing
 * the associated alarms.
 */
class SessionResetReceiver : BroadcastReceiver() {

    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast for session reset.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_RESET_SESSION -> {
                SessionTimer.reset()
                setAlarmSetFlag(false)
                Log.d("SessionReset", "Reset triggered")
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 0
        const val ACTION_RESET_SESSION = "com.teamdelta.screentime.RESET_SESSION"
        private const val RESET_LIMIT_MINS = 1

        /**
         * Schedules a new session reset alarm.
         *
         * @param context The context used to access system services and set the alarm.
         */
        fun schedule(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, SessionResetReceiver::class.java).apply {
                action = ACTION_RESET_SESSION
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!alarmManager.canScheduleExactAlarms()) {
                // Fall back to inexact alarm if permission not granted
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + RESET_LIMIT_MINS * 60 * 1000,
                    pendingIntent
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + RESET_LIMIT_MINS * 60 * 1000,
                    pendingIntent
                )
            }
            setAlarmSetFlag(true)
            Log.d("SessionReset", "Alarm set")
        }


        /**
         * Cancels the currently scheduled session reset alarm.
         *
         * @param context The context used to access system services and cancel the alarm.
         */
        fun cancel(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, SessionResetReceiver::class.java).apply {
                action = ACTION_RESET_SESSION
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
            setAlarmSetFlag(false)
            Log.d("SessionReset", "Alarm canceled")
        }
    }
}
