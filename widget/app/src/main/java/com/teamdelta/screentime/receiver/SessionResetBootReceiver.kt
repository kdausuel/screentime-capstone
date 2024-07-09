package com.teamdelta.screentime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver for rescheduling alarms after device reboot.
 *
 * This receiver is triggered when the device completes booting and reschedules
 * any previously set alarms.
 */
class SessionResetBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule the alarm if it was set before reboot
            if (SessionResetReceiver.wasAlarmSet(context)) {
                SessionResetReceiver.schedule(context)
            }
        }
    }
}