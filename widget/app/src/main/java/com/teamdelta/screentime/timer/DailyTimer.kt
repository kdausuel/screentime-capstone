package com.teamdelta.screentime.timer

import android.content.Context
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.notify.NotificationActivity
import com.teamdelta.screentime.notify.NotificationLauncher

/**
 * Object representing the daily timer.
 *
 * This object manages the state and operations related to the daily timer.
 */
object DailyTimer {
    private const val TIMER_ID = "daily"

    val currentValue: Int?
        get() = DataManager.getTimerCurrentValue(TIMER_ID)

    val limit: Int?
        get() = DataManager.getTimerLimit(TIMER_ID)

    var isRunning: Boolean
        get() = DataManager.isTimerRunning(TIMER_ID)!!
        set(value) = DataManager.setTimerRunning(TIMER_ID, value)!!

    fun setLimit(value: Int) = DataManager.setTimerLimit(TIMER_ID, value)

    fun updateCurrentValue(value: Int) = DataManager.setTimerCurrentValue(TIMER_ID, value)

    fun isLimitReached(): Boolean = currentValue == 0

    fun reset() {
        DataManager.setTimerCurrentValue(TIMER_ID, limit ?:-1)
    }
}