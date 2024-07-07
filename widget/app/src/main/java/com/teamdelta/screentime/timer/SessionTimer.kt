package com.teamdelta.screentime.timer

import com.teamdelta.screentime.data.DataManager

object SessionTimer {
    private const val TIMER_ID = "session"

    val currentValue: Int
        get() = DataManager.getTimerCurrentValue(TIMER_ID)

    val limit: Int
        get() = DataManager.getTimerLimit(TIMER_ID)

    var isRunning: Boolean
        get() = DataManager.isTimerRunning(TIMER_ID)
        set(value) = DataManager.setTimerRunning(TIMER_ID, value)

    fun setLimit(value: Int) = DataManager.setTimerLimit(TIMER_ID, value)

    fun updateCurrentValue(value: Int) = DataManager.setTimerCurrentValue(TIMER_ID, value)

    fun isLimitReached(): Boolean = currentValue <= limit

    fun reset() {
        DataManager.setTimerCurrentValue(TIMER_ID, 0)
        DataManager.setTimerRunning(TIMER_ID, false)
    }
}