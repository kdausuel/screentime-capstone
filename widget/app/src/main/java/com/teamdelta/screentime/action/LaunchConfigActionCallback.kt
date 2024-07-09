package com.teamdelta.screentime.action

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.teamdelta.screentime.ui.config.ConfigActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Callback for launching the configuration activity.
 *
 * This class is responsible for handling the action to launch the configuration
 * activity when triggered from the widget.
 */
class LaunchConfigActionCallback : ActionCallback {

    /**
    * Executes the action to launch the configuration activity.
    *
    * @param context The context in which the action is being performed.
    * @param glanceId The ID of the Glance widget triggering this action.
    * @param parameters Additional parameters for the action (not used in this implementation).
    */
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = Intent(context, ConfigActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}