package com.teamdelta.screentime.ui.config

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.timer.DailyTimer
import com.teamdelta.screentime.timer.SessionTimer
import com.teamdelta.screentime.timer.TimerManager
import androidx.lifecycle.lifecycleScope
import com.teamdelta.screentime.receiver.ScreenStateManager
//import com.teamdelta.screentime.timer.TimerManager.appContext
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget
import com.teamdelta.screentime.worker.DailyResetScheduler
import com.teamdelta.screentime.worker.DailyResetWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity for configuring the Screen Time widget.
 *
 * This activity allows users to set up and modify timer limits and other widget settings.
 */
class ConfigActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Config", "Started")
        checkOverlayPermission(this)
        checkAlarmPermission(this)

        // Check if this is a reconfiguration request
        val isReconfiguring = intent?.flags?.and(Intent.FLAG_ACTIVITY_NEW_TASK) != 0
        Log.d("ConfigActivity", "isReconfiguring: $isReconfiguring")

        // Retrieve the AppWidget ID from the Intent that launched the Activity
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If the activity was started without an app widget ID, finish it.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !isReconfiguring) {
            Log.d("Config", "INVALID ID and not reconfiguring")
            finish()
            return
        }
        setResult(RESULT_CANCELED)

        ScreenStateManager.initialize(applicationContext)

        lifecycleScope.launch(Dispatchers.Default) {
            DataManager.initialize(applicationContext)
            Log.d("ConfigActivity", "DataManager initialized")

            if (DataManager.getConfig() == true && !isReconfiguring) {
                Log.d("ConfigActivity", "Widget already configured and not reconfiguring")
                // Widget already configured, just update and finish
                // or if launched from the ActionCallback display UI
                ScreenTimeGlanceWidget.updateWidget(applicationContext)
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
                finish()
            } else {
                Log.d("ConfigActivity", "Showing UI")
                withContext(Dispatchers.Main) {
                    showUI()
                }
            }
        }
    }

    // Config UI
    private fun showUI() {
        setContent {
            var dailyValue by remember { mutableIntStateOf(10) }
            var sessionValue by remember { mutableIntStateOf(10) }
            Column {
                TextField(
                    // Daily Timer
                    value = dailyValue.toString(),
                    onValueChange = {
                        dailyValue = it.toIntOrNull() ?: 10
                    },
                    label = { Text("Daily Timer") }
                )
                TextField(
                    //Session Timer
                    value = sessionValue.toString(),
                    onValueChange = {
                        sessionValue = it.toIntOrNull() ?: 10
                    },
                    label = { Text("Session Timer") }
                )
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            //think about if I want to just have the new values change upon reset
                            //or immediately

                            DailyTimer.setLimit(dailyValue)
                            SessionTimer.setLimit(sessionValue)
                            DailyTimer.limit?.let { DailyTimer.updateCurrentValue(it) }
                            SessionTimer.limit?.let { SessionTimer.updateCurrentValue(it) }
                            Log.d("ConfigActivity", "Timers set")
                            ScreenTimeGlanceWidget.updateWidget(applicationContext)
                            Log.d("ConfigActivity", "Widget updated")
                            DataManager.setConfig(true)
                            Log.d("ConfigActivity", "Config set")
                            // Set the result to OK and include the widget ID
                            val resultVal = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            setResult(RESULT_OK, resultVal)
                            if (!(DailyTimer.isRunning && SessionTimer.isRunning)){
                                DailyTimer.isRunning = true
                                SessionTimer.isRunning = true
                            }
                            TimerManager.initialize(applicationContext)
                            Log.d("ConfigActivity", "TimerManager initialized")
                            DailyResetScheduler.scheduleDailyResetAtMidnight(applicationContext)
                            Log.d("ConfigActivity", "Setup midnight reset task")
                            finish()
                        }
                    }
                ){
                    Text("Save Changes")
                }
                Button(
                    onClick = {finish()}
                ){
                    Text("Cancel")
                }
            }
        }
    }
/*
    private suspend fun updateWidget() {
        try {
            val glanceManager = GlanceAppWidgetManager(this@ConfigActivity)
            val glanceIds = glanceManager.getGlanceIds(ScreenTimeGlanceWidget::class.java)
            glanceIds.forEach { glanceId ->
                ScreenTimeGlanceWidget.updateWidget(this@ConfigActivity, glanceId)
            }
        } catch (e: Exception) {
            // Log the error
            e.printStackTrace()
        }
    }
*/
}

fun checkOverlayPermission(context: Context) {
    if (!Settings.canDrawOverlays(context)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
fun checkAlarmPermission(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}


/*
    companion object{
        private var IS_CONFIGURED : Boolean = false

        fun setConfig(status :Boolean){
            DataManager.setConfig(status)
        }

        fun getConfig() : Boolean{
            return DataManager.get
        }
    }
 */