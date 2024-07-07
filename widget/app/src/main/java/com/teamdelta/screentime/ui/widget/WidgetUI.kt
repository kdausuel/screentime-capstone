package com.teamdelta.screentime.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.teamdelta.screentime.R
import com.teamdelta.screentime.action.LaunchConfigActionCallback
import java.util.Locale

object WidgetUI {
    @Composable
    fun Content() {
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
                        label = "Daily",
                        time = "12:34:56", // Replace with actual daily timer value
                        modifier = GlanceModifier.defaultWeight()
                    )
                    TimerDisplay(
                        label = "Session",
                        time = "01:23:45", // Replace with actual session timer value
                        modifier = GlanceModifier.defaultWeight()
                    )
                }

                // Settings gear icon
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    androidx.glance.Image(
                        provider = ImageProvider(R.drawable.icon_settings),
                        contentDescription = "Settings",
                        modifier = GlanceModifier
                            .padding(8.dp)
                            .clickable(actionRunCallback<LaunchConfigActionCallback>())
                    )
                }
            }
        }
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> String.format(Locale.US,"%d:%02d", hours, minutes)
            else -> String.format(Locale.US,"%02d:%02d", minutes, remainingSeconds)
        }
    }

    @Composable
    private fun TimerDisplay(label: String, time: String, modifier: GlanceModifier = GlanceModifier) {
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
