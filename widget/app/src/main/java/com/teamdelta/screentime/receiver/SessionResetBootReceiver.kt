package com.teamdelta.screentime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.teamdelta.screentime.data.DataManager

/**
 * BroadcastReceiver for rescheduling alarms after device reboot.
 *
 * This receiver is triggered when the device completes booting and reschedules
 * any previously set alarms.
 */
class SessionResetBootReceiver : BroadcastReceiver() {

    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast for device boot completion.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule the alarm if it was set before reboot
            if (DataManager.wasAlarmSet() == true) {
                SessionResetReceiver.schedule(context)
            }
        }
    }
}
