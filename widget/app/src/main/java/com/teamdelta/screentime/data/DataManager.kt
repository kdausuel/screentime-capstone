package com.teamdelta.screentime.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton object for managing shared preferences data.
 *
 * This object provides methods to initialize, reset, and access shared preferences
 * used throughout the application.
 */
object DataManager {
    private  var prefs: SharedPreferences? = null
    private const val PREF_NAME : String = "screen_time_prefs"
    private const val DEFAULT_LIMIT : Int = 0 // 0 seconds default
    private var IS_INITIALIZED : Boolean = false

    /**
     * Initializes the DataManager with the given context.
     *
     * @param context The context used to get shared preferences.
     */
    @Synchronized
    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            IS_INITIALIZED = true

        }
    }

    /**
     * Resets all shared preferences to their default values.
     *
     * @param context The context used to get shared preferences.
     */
    fun reset(context: Context) {
        initialize(context)
        prefs?.edit()?.clear()?.apply()
        IS_INITIALIZED = false

    }

    fun isInitialized() : Boolean {
        return IS_INITIALIZED
    }

    fun setConfig(status:Boolean){
        prefs?.edit()?.putBoolean("config", status)?.apply()
    }
    fun getConfig() : Boolean? {return prefs?.getBoolean("config", false)}

    fun getTimerCurrentValue(timerId: String): Int? =
        prefs?.getInt("${timerId}_current", 0)

    fun setTimerCurrentValue(timerId: String, value: Int) =
        prefs?.edit()?.putInt("${timerId}_current", value)?.apply()

    fun getTimerLimit(timerId: String): Int? =
        prefs?.getInt("${timerId}_limit", DEFAULT_LIMIT)

    fun setTimerLimit(timerId: String, value: Int) =
        prefs?.edit()?.putInt("${timerId}_limit", value)?.apply()

    fun isTimerRunning(timerId: String): Boolean? =
        prefs?.getBoolean("${timerId}_running", false)

    fun setTimerRunning(timerId: String, isRunning: Boolean) =
        prefs?.edit()?.putBoolean("${timerId}_running", isRunning)?.apply()

    fun setAlarmSetFlag(isSet: Boolean) {
        prefs?.edit()?.putBoolean("alarmWasSet", isSet)?.apply()
    }

    fun wasAlarmSet(): Boolean? { return prefs?.getBoolean("alarmWasSet", false) }
}