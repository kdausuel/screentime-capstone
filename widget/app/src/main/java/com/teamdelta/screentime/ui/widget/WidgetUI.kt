package com.teamdelta.screentime.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.teamdelta.screentime.R
import com.teamdelta.screentime.action.LaunchConfigActionCallback
import com.teamdelta.screentime.ui.TimeDisplayUtility.formatTime
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget.dailyTime
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget.sessionTime

/**
 * Object containing UI components and functions for the ScreenTime widget.
 */
object WidgetUI {

    /**
     * Function that displays the main content for the ScreenTime widget.
     * It includes two timer displays for the daily and session timer and a settings icon.
     */
    @Composable
    fun Content() {
        val dailyTime = currentState(key = dailyTime) ?: 0
        val sessionTime = currentState(key = sessionTime) ?: 0
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.background)
            ) {
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TimerDisplay(
                        // Daily Timer display
                        label = "Daily",
                        time = formatTime(dailyTime),
                        modifier = GlanceModifier.defaultWeight()
                    )
                    TimerDisplay(
                        // Session Timer display
                        label = "Session",
                        time = formatTime(sessionTime),
                        modifier = GlanceModifier.defaultWeight()
                    )
                }

                // Settings icon/button functionality
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    androidx.glance.Image(
                        provider = ImageProvider(R.drawable.icon_settings),
                        contentDescription = "Settings",
                        modifier = GlanceModifier
                            .padding(8.dp)
                            .clickable(actionRunCallback(LaunchConfigActionCallback::class.java))
                    )
                }
            }
        }
    }

    /**
     * A function that displays a timer with a label and the time.
     *
     * @param label The label text to be displayed above the time.
     * @param time The time text to be displayed below the label.
     * @param modifier A [GlanceModifier] to be applied to the
     *  Column container.Defaults to [GlanceModifier].
     */
    @Composable
    private fun TimerDisplay(
        label: String,
        time: String,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Column(
            modifier = modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontSize = 14.sp
                )
            )
            Text(
                text = time,
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 18.sp
                )
            )
        }
    }
}
