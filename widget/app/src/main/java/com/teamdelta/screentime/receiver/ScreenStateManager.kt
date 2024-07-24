package com.teamdelta.screentime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.teamdelta.screentime.timer.TimerManager

/**
 * Object for managing screen state changes.
 *
 * This object handles screen on/off events and updates timers accordingly, and manages
 * the scheduling of session reset alarms based on screen state.
 */
object ScreenStateManager {
    private var receiver: BroadcastReceiver? = null

    /**
     * Initializes the ScreenStateManager by registering a BroadcastReceiver for screen state changes.
     *
     * @param context The context used to register the BroadcastReceiver.
     */
    fun initialize(context: Context) {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_ON -> { // Handle screen on
                        TimerManager.pauseTimers(false)
                        SessionResetReceiver.cancel(context)
                        Log.d("ScreenState", "Screen On")
                    }
                    Intent.ACTION_SCREEN_OFF -> { // Handle screen off
                        TimerManager.pauseTimers(true)
                        SessionResetReceiver.schedule(context)
                        Log.d("ScreenState", "Screen Off")
                    }
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    /**
     * Cleans up resources by unregistering the BroadcastReceiver.
     *
     * This should be called when the ScreenStateManager is no longer needed to prevent memory leaks.
     *
     * @param context The context used to unregister the BroadcastReceiver.
     */
    fun cleanup(context: Context) {
        receiver?.let {
            context.unregisterReceiver(it)
            receiver = null
        }
    }
}
