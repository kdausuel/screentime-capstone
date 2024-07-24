package com.teamdelta.screentime.timer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.notify.NotificationActivity
import com.teamdelta.screentime.notify.NotificationLauncher
import com.teamdelta.screentime.ui.config.ConfigActivity
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Object for managing all timers in the ScreenTime application.
 *
 * This object handles updating timers, initializing the timer system,
 * and coordinating between different timer types. It uses coroutines for
 * asynchronous operations and a Handler for periodic updates.
 *
 * Key features:
 * - Initialization of the timer system
 * - Periodic timer updates using a Handler
 * - Management of daily and session timers
 * - Widget updates
 * - Notification triggering when limits are reached
 */
object TimerManager {
    val timerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var appContext : Context
    private var isInitialized = false

    /**
     * Initializes the TimerManager with the given context.
     *
     * This method should be called once when the application starts.
     * It sets up the periodic timer update mechanism.
     *
     * @param context The context used for accessing application resources and services.
     */
    fun initialize(context: Context){
        if (!isInitialized) {
            appContext = context
            handler.post(timerRunnable)
            isInitialized = true
        }
    }

    /**
     * Runnable that performs periodic timer updates.
     * It updates both daily and session timers, checks for limits,
     * triggers notifications if needed, and updates the widget.
     */
    private val timerRunnable = object : Runnable {
        override fun run(){
            if (::appContext.isInitialized){
                Log.d("TimerManager", "Runnable started")
                timerScope.launch {
                    val widgetManager = GlanceAppWidgetManager(appContext)
                    widgetManager.getGlanceIds(ScreenTimeGlanceWidget::class.java)
                        .forEach {
                            glanceId ->
                            if (DataManager.getConfig() == true){
                                Log.d("TimerManager", "Updating timers")
                                updateTimers(glanceId)
                            }
                        }
                }
                handler.postDelayed(this, 1000)
            }
        }
    }

    /**
     * Updates the timers and the widget display.
     *
     * This method is called periodically to decrement timer values,
     * check for limit reached conditions, and update the widget UI.
     *
     * @param id The GlanceId of the widget to update.
     */
    suspend fun updateTimers(id: GlanceId) {
        withContext(Dispatchers.Default) {
            if (DailyTimer.isRunning) {
                Log.d("TimerManager", "Updating Daily timer --${DailyTimer.currentValue?.minus(1)}")
                DailyTimer.updateCurrentValue(DailyTimer.currentValue?.minus(1) ?: 0)
                if (DailyTimer.isLimitReached()) {
                    withContext(Dispatchers.Main) {
                        NotificationLauncher.launchNotify(appContext, "daily")
                    }
                }
            }
            if (SessionTimer.isRunning) {
                SessionTimer.updateCurrentValue(SessionTimer.currentValue?.minus(1) ?: 0)
                if (SessionTimer.isLimitReached()) {
                    withContext(Dispatchers.Main) {
                        NotificationLauncher.launchNotify(appContext, "session")
                    }
                }
            }
            ScreenTimeGlanceWidget.updateWidget(appContext)
            Log.d("TimerManager", "Updated widget")
        }
    }

    /**
     * Stops all running timers and cancels any pending updates.
     *
     * This method should be called when the widget is being removed or the app is shutting down.
     */
    fun terminateAllTimers(){
        handler.removeCallbacks(timerRunnable)
    }

    /**
     * Pauses or resumes all timers based on the given status.
     *
     * @param status True to pause timers, false to resume.
     */
    fun pauseTimers(status : Boolean){
        DailyTimer.isRunning = !status
        SessionTimer.isRunning = !status
    }

}
