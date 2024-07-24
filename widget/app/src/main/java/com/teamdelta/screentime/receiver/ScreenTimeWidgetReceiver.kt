package com.teamdelta.screentime.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.timer.TimerManager
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget

/**
 * GlanceAppWidgetReceiver for the Screen Time widget.
 *
 * This receiver handles widget-related events such as updates and configuration changes.
 */
class ScreenTimeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() {
            Log.d("ScreenTimeWidgetReceiver", "glanceAppWidget getter called")
            return ScreenTimeGlanceWidget
        }

    /**
     * Called when the first instance of the widget is created.
     * 
     * @param context The Context in which the receiver is running.
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d("ScreenTimeWidgetReceiver", "onEnabled called")
    }

    /**
     * Called when the AppWidgetManager notifies the receiver to update the widget.
     * 
     * @param context The Context in which the receiver is running.
     * @param appWidgetManager The AppWidgetManager instance.
     * @param appWidgetIds An array of all widget instance IDs to update.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("ScreenTimeWidgetReceiver", "onUpdate called")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    /**
     * Called when the last instance of the widget is removed from the home screen.
     *
     * This method performs cleanup tasks such as stopping timers and resetting data.
     *
     * @param context The Context in which the receiver is running.
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d("ScreenTimeWidgetReceiver", "onDisabled called")
        ScreenStateManager.cleanup(context)
        DataManager.reset(context)
        TimerManager.terminateAllTimers()

    }

    /**
     * Called when a particular instance of the widget is deleted from the home screen.
     * 
     * @param context The Context in which the receiver is running.
     * @param appWidgetIds An array of IDs for the deleted widget instances.
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d("ScreenTimeWidgetReceiver", "onDeleted called")
    }
}

