package com.teamdelta.screentime.data

import android.content.Context
import android.content.SharedPreferences


object DataManager {
    private lateinit var prefs: SharedPreferences
    private const val PREF_NAME : String = "screen_time_prefs"
    private const val DEFAULT_LIMIT : Int = 0 // 0 seconds default

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        //prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun reset(){prefs.edit().clear().apply()}

    fun setConfig(status:Boolean){
        prefs.edit().putBoolean("config", status).apply()
    }
    fun getConfig() : Boolean{return prefs.getBoolean("config", false)}

    fun getTimerCurrentValue(timerId: String): Int =
        prefs.getInt("${timerId}_current", 0)

    fun setTimerCurrentValue(timerId: String, value: Int) =
        prefs.edit().putInt("${timerId}_current", value).apply()

    fun getTimerLimit(timerId: String): Int =
        prefs.getInt("${timerId}_limit", DEFAULT_LIMIT)

    fun setTimerLimit(timerId: String, value: Int) =
        prefs.edit().putInt("${timerId}_limit", value).apply()

    fun isTimerRunning(timerId: String): Boolean =
        prefs.getBoolean("${timerId}_running", false)

    fun setTimerRunning(timerId: String, isRunning: Boolean) =
        prefs.edit().putBoolean("${timerId}_running", isRunning).apply()

    fun getLong(key: String, defaultValue: Long): Long = prefs.getLong(key, defaultValue)
    fun setLong(key: String, value: Long) = prefs.edit().putLong(key, value).apply()
}