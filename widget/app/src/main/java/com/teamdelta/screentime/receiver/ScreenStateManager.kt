package com.teamdelta.screentime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.teamdelta.screentime.timer.TimerManager


object ScreenStateManager {
    private var receiver: BroadcastReceiver? = null

    fun initialize(context: Context) {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> { // Handle screen on
                        TimerManager.pauseTimers(false)
                        Log.d("ScreenState", "Screen On")
                    }
                    Intent.ACTION_SCREEN_OFF -> { // Handle screen off
                        TimerManager.pauseTimers(true)
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

    fun cleanup(context: Context) {
        receiver?.let {
            context.unregisterReceiver(it)
            receiver = null
        }
    }
}

// Usage
//ScreenStateManager.initialize(context)
// Later
//ScreenStateManager.cleanup(context)
