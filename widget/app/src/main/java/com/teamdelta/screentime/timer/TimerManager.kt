package com.teamdelta.screentime.timer

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.ui.config.ConfigActivity
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


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
                timerScope.launch {
                    val widgetManager = GlanceAppWidgetManager(appContext)
                    widgetManager.getGlanceIds(ScreenTimeGlanceWidget::class.java)
                        .forEach {
                            glanceId ->
                            if (DataManager.getConfig()){
                                updateTimers()
                            }
                        }
                }
                handler.postDelayed(this, 1000)
            }
        }
    }

    fun updateTimers() {
        if (DailyTimer.isRunning) {  //&& DailyTimer.isLimitReached()
            DailyTimer.updateCurrentValue(DailyTimer.currentValue - 1)
        }
        if (SessionTimer.isRunning) {
            SessionTimer.updateCurrentValue(SessionTimer.currentValue - 1)
        }
    }

    fun pauseTimers(){
        //move to broadcast receiver
        DailyTimer.isRunning = false
        SessionTimer.isRunning = false
    }

}