package com.teamdelta.screentime.timer

import android.provider.ContactsContract.Data
import com.teamdelta.screentime.data.DataManager

abstract class Timer(private val timerId : String) {

    fun reset() {
        DataManager.setTimerCurrentValue(timerId, limit ?:-1)
    }

    val currentValue : Int?
        get() = DataManager.getTimerCurrentValue(timerId)

    val limit: Int?
        get() = DataManager.getTimerLimit(timerId)

    var isRunning: Boolean
        get() = DataManager.isTimerRunning(timerId)!!
        set(value) = DataManager.setTimerRunning(timerId, value)!!

    fun setLimit(value: Int) = DataManager.setTimerLimit(timerId, value)

    fun updateCurrentValue(value: Int) = DataManager.setTimerCurrentValue(timerId, value)

    fun isLimitReached(): Boolean = currentValue == 0
}