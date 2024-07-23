package com.teamdelta.screentime.timer

import android.provider.ContactsContract.Data
import com.teamdelta.screentime.data.DataManager

/**
 * Abstract base class for timers in the ScreenTime application.
 *
 * This class provides common functionality for different types of timers,
 * including persistence of timer state using DataManager.
 *
 * @property timerId The unique identifier for this timer type.
 */
abstract class Timer(private val timerId : String) {

    /**
     * Resets the timer to its initial limit value.
     */
    fun reset() {
        DataManager.setTimerCurrentValue(timerId, limit ?:-1)
    }

    /**
     * The current value of the timer in seconds.
     */
    val currentValue : Int?
        get() = DataManager.getTimerCurrentValue(timerId)

    /**
     * The limit value of the timer in seconds.
     */
    val limit: Int?
        get() = DataManager.getTimerLimit(timerId)

    /**
     * Whether the timer is currently running or paused.
     */
    var isRunning: Boolean
        get() = DataManager.isTimerRunning(timerId)!!
        set(value) = DataManager.setTimerRunning(timerId, value)!!

    /**
     * Sets the limit for this timer.
     *
     * @param value The new limit value in seconds.
     */
    fun setLimit(value: Int) = DataManager.setTimerLimit(timerId, value)

    /**
     * Updates the current value of the timer.
     *
     * @param value The new current value in seconds.
     */
    fun updateCurrentValue(value: Int) = DataManager.setTimerCurrentValue(timerId, value)

    /**
     * Checks if the timer has reached its limit (i.e., current value is zero).
     *
     * @return True if the limit is reached, false otherwise.
     */
    fun isLimitReached(): Boolean = currentValue == 0
}
