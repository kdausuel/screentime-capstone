package com.teamdelta.screentime.ui

import android.util.Log
import java.util.Locale

object TimeDisplayUtility{

    fun convertFromSec(seconds : Int) : Triple<Int, Int,Int> {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return Triple(hours, minutes, remainingSeconds)
    }

    fun convertToSec(hours : Int, mins : Int) : Int{ return (hours * 3600) + (mins * 60) }

    fun formatTime(seconds: Int): String {
        val (hours,minutes, remainingSeconds) = convertFromSec(seconds)
        Log.d("Format time", "Daily val: $seconds")

        return when {
            hours > 0 -> String.format(Locale.US,"%d:%02d", hours, minutes)
            else -> String.format(Locale.US,"%02d:%02d", minutes, remainingSeconds)
        }
    }
}