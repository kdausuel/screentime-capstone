package com.teamdelta.screentime.ui.widget

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
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

    /**
     * Provides the content for the Glance widget.
     *
     * @param context The context in which the widget is being displayed.
     * @param id The unique identifier for this instance of the widget.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("ScreenTime", "provideGlance called for id: $id")
        provideContent {

            WidgetUI.Content()
        }
    }

    /**
     * Updates the widget state and display.
     *
     * This method should be called whenever the widget needs to be refreshed,
     * such as when timer values change.
     *
     * @param context The context used for accessing application resources and services.
     */
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
