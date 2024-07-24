package com.teamdelta.screentime.timer

/**
 * Object representing the session timer.
 *
 * This object manages the state and operations related to the session timer. This timer tracks continuous screen time for a single session.
 */
object SessionTimer : Timer("session") {

}

/**
 * Object representing the daily timer.
 *
 * This object manages the state and operations related to the daily timer. This timer tracks total screen time for the current day.
 */
object DailyTimer : Timer("daily"){

}
