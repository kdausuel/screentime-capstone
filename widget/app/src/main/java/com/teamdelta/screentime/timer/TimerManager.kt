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


object TimerManager {
    val timerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var appContext : Context

    fun initialize(context: Context){
        appContext = context
        handler.post(timerRunnable)
    }

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

/*
    suspend fun updateTimers(id: GlanceId) {
        if (DailyTimer.isRunning) {  //&& DailyTimer.isLimitReached()
            Log.d("TimerManager", "Updating Daily timer --${DailyTimer.currentValue - 1}")

            DailyTimer.updateCurrentValue(DailyTimer.currentValue - 1)
            if (DailyTimer.isLimitReached()){
                DailyTimer.launchNotify(appContext)
            }
        }
        if (SessionTimer.isRunning) {
            SessionTimer.updateCurrentValue(SessionTimer.currentValue - 1)
            if (SessionTimer.isLimitReached()){
                SessionTimer.launchNotify(appContext)
            }
        }
        ScreenTimeGlanceWidget.updateWidget(appContext)
        Log.d("TimerManager", "Updated widget")
    }
*/
    fun terminateAllTimers(){
        handler.removeCallbacks(timerRunnable)
    }

    fun pauseTimers(status : Boolean){
        //move to broadcast receiver
        DailyTimer.isRunning = !status
        SessionTimer.isRunning = !status
    }

}