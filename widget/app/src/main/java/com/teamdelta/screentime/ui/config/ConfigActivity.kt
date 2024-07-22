package com.teamdelta.screentime.ui.config

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.timer.DailyTimer
import com.teamdelta.screentime.timer.SessionTimer
import com.teamdelta.screentime.timer.TimerManager
import androidx.lifecycle.lifecycleScope
import com.teamdelta.screentime.receiver.ScreenStateManager
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget
import com.teamdelta.screentime.worker.DailyResetScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity for configuring the ScreenTime widget.
 *
 * This activity allows users to set up and modify timer limits and other widget settings.
 * It handles the initialization of necessary permissions and displays the configuration UI.
 */
class ConfigActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Config", "Started")
        checkOverlayPermission(this)
        checkAlarmPermission(this)

        // check if this is a reconfiguration request
        val isReconfiguring = intent?.flags?.and(Intent.FLAG_ACTIVITY_NEW_TASK) != 0
        Log.d("ConfigActivity", "isReconfiguring: $isReconfiguring")

        // retrieve AppWidgetID from the intent that launched the activity
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If the activity was started without an app widget ID, finish it
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
                // Widget already configured, just update and finish
                Log.d("ConfigActivity", "Widget already configured and not reconfiguring")
                ScreenTimeGlanceWidget.updateWidget(applicationContext)
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
                finish()
            } else {
                // if launched from the ActionCallback display UI
                Log.d("ConfigActivity", "Showing UI")
                withContext(Dispatchers.Main) {
                    setContent {
                        var changedDailyTimer  by remember { mutableStateOf(false) }
                        var changedSessionTimer  by remember { mutableStateOf(false)}

                        ConfigUI.Content(
                            onSave = {clickSaveButton(changedDailyTimer, changedSessionTimer)},
                            onCancel = {finish()},
                            changedDailyTimer = changedDailyTimer,
                            changedSessionTimer = changedSessionTimer,
                            onTimerChange = { daily, session ->
                                changedDailyTimer = daily
                                changedSessionTimer = session
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * Handles saving the configuration settings.
     *
     */
    private fun clickSaveButton(changedDaily : Boolean, changedSession: Boolean){
        lifecycleScope.launch {
            //think about if I want to just have the new values change upon reset
            //or immediately
            if (changedDaily) {
                DailyTimer.limit?.let { DailyTimer.updateCurrentValue(it) }
            }
            if (changedSession) {
                SessionTimer.limit?.let { SessionTimer.updateCurrentValue(it) }
            }
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
}

/**
 * Checks and requests overlay permission if not granted.
 *
 * @param context The context of the application.
 */
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

/**
 * Checks and requests alarm permission if not granted.
 *
 * @param context The context of the application.
 */
fun checkAlarmPermission(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}