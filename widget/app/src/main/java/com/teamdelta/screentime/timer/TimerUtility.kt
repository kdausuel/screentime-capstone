package com.teamdelta.screentime.timer

import com.teamdelta.screentime.data.DataManager

class TimerUtility {
    companion object {
        fun getCurrentValue(timerId: String): Int =
            DataManager.getTimerCurrentValue(timerId)

        fun getLimit(timerId: String): Int =
            DataManager.getTimerLimit(timerId)

        fun isRunning(timerId: String): Boolean =
            DataManager.isTimerRunning(timerId)

        fun setLimit(timerId: String, value: Int) =
            DataManager.setTimerLimit(timerId, value)

        fun updateCurrentValue(timerId: String, value: Int) =
            DataManager.setTimerCurrentValue(timerId, value)

        fun setRunning(timerId: String, isRunning: Boolean) =
            DataManager.setTimerRunning(timerId, isRunning)

        fun isLimitReached(timerId: String): Boolean =
            getCurrentValue(timerId) >= getLimit(timerId)

        fun reset(timerId: String) {
            updateCurrentValue(timerId, 0)
            setRunning(timerId, false)
        }
    }
}