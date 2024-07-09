package com.teamdelta.screentime.ui.widget

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.teamdelta.screentime.timer.DailyTimer
import com.teamdelta.screentime.timer.SessionTimer

/**
 * GlanceAppWidget for the ScreenTime widget.
 *
 * This object defines the structure and behavior of the ScreenTime widget.
 */
object ScreenTimeGlanceWidget : GlanceAppWidget() {
    val dailyTime = intPreferencesKey("daily_time")
    val sessionTime = intPreferencesKey("session_time")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("ScreenTime", "provideGlance called for id: $id")
        provideContent {

            WidgetUI.Content()
        }
    }

    suspend fun updateWidget(context: Context) {
        Log.d("ScreenTime", "updateWidget called")
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(ScreenTimeGlanceWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[dailyTime] = DailyTimer.currentValue?:0
                    prefs[sessionTime] = SessionTimer.currentValue?:0
                }
            update(context, glanceId)
            }
        }
}
