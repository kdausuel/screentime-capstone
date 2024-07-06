package com.teamdelta.screentime.timer


class TimerManager() {

    fun updateTimers() {
        if (DailyTimer.isRunning) {
            DailyTimer.updateCurrentValue(DailyTimer.currentValue - 1)
        }
        if (SessionTimer.isRunning) {
            SessionTimer.updateCurrentValue(SessionTimer.currentValue - 1)
        }
    }

}