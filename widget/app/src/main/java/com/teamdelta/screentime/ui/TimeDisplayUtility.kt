package com.teamdelta.screentime.ui

import android.util.Log
import java.util.Locale

/**
 * Utility object for time-related conversion operations and formatting the time on the UI.
 *
 * This object provides methods to convert between seconds and hours/minutes/seconds,
 * as well as formatting time for display purposes.
 */
object TimeDisplayUtility{

    /**
     * Converts seconds to hours, minutes, and seconds.
     *
     * @param seconds The total number of seconds to convert.
     * @return A Triple containing hours, minutes, and seconds.
     */
    fun convertFromSec(seconds : Int) : Triple<Int, Int,Int> {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return Triple(hours, minutes, remainingSeconds)
    }

    /**
     * Converts hours and minutes to total seconds.
     *
     * @param hours The number of hours.
     * @param mins The number of minutes.
     * @return The total number of seconds.
     */
    fun convertToSec(hours : Int, mins : Int) : Int{ return (hours * 3600) + (mins * 60) }

    /**
     * Formats a given number of seconds into a human-readable string.
     *
     * The format varies based on the magnitude of the time:
     * - For times greater than or equal to an hour: "X hr(s) YY min(s)"
     * - For times less than an hour: "YY min(s) ZZ sec(s)"
     *
     * Negative times are prefixed with a minus sign.
     *
     * @param seconds The number of seconds to format.
     * @return A formatted string representing the time.
     */
    fun formatTime(seconds: Int): String {
        val (hours,minutes, remainingSeconds) = convertFromSec(kotlin.math.abs(seconds))
        var hr = if (hours == 1) "hr" else "hrs"
        var min = if (minutes == 1) "min" else "mins"
        var sec = if (remainingSeconds == 1) "sec" else "secs"
        var negativeTime = if (seconds < 0) "-" else ""
        Log.d("Format time", "Daily val: $seconds")

        return when {
            hours > 0 -> String.format(Locale.US,"$negativeTime %d $hr %02d $min", hours, minutes)
            else -> String.format(Locale.US,"$negativeTime %02d $min %02d $sec", minutes, remainingSeconds)
        }
    }
}
