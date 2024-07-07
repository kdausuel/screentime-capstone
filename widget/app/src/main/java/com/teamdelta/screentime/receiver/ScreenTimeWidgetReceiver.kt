package com.teamdelta.screentime.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget

class ScreenTimeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() {
            Log.d("ScreenTimeWidgetReceiver", "glanceAppWidget getter called")
            return ScreenTimeGlanceWidget()
        }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d("ScreenTimeWidgetReceiver", "onEnabled called")
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("ScreenTimeWidgetReceiver", "onUpdate called")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d("ScreenTimeWidgetReceiver", "onDisabled called")
    }
}
