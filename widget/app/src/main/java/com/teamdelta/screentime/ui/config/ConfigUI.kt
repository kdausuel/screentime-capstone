package com.teamdelta.screentime.ui.config

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.timer.DailyTimer
import com.teamdelta.screentime.timer.SessionTimer
import com.teamdelta.screentime.ui.TimeDisplayUtility
import com.teamdelta.screentime.ui.TimeDisplayUtility.formatTime

/**
 * Object containing UI components and functions for the Configuration activity.
 * Provides the UI for configuring the Screen Time widget.
 */

object ConfigUI {

    /**
     * Function for displaying the UI for setting the timers.
     *
     * @param onSave Callback function to handle saving the configuration.
     * @param onCancel Callback function to handle canceling the configuration.
     */
    @Composable
    fun Content(
        onSave : () -> Unit,
        onCancel : () -> Unit
    ){
        var isButtonEnabled  by remember { mutableStateOf(false)}
        Column {
            TimerInterface(type = "daily", onLimitChange = {
                isButtonEnabled =
                    ((DailyTimer.limit ?: 0) > 0 &&
                            (DailyTimer.limit ?: 0) > (SessionTimer.limit ?: 0)) &&
                            (SessionTimer.limit ?: 0) > 0
            })
            TimerInterface("session", onLimitChange = {
                isButtonEnabled = ((DailyTimer.limit ?: 0) > 0
                        && (DailyTimer.limit ?: 0) > (SessionTimer.limit ?: 0))
                        && (SessionTimer.limit ?: 0) > 0
            })
            Button(
                onClick = onSave,
                enabled = isButtonEnabled
            ){
                Text("Save Changes")
            }
            Button(
                onClick = onCancel
            ){
                Text("Cancel")
            }
        }
    }

    /**
     * Composable function for displaying and managing a timer interface.
     *
     * @param type The type of timer ("daily" or "session").
     * @param onLimitChange Callback function to determine if the Save button should be clickable
     */
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun TimerInterface(type : String, onLimitChange : () -> Unit) {

        val timerName = type.replaceFirstChar(Char::titlecase)
        var timerValue by remember { mutableStateOf(DataManager.getTimerLimit(type) ?: 0) }
        var validTimerLimit by remember{ mutableStateOf(false)}
        val (initialHour, initialMinute, _) = TimeDisplayUtility.convertFromSec(timerValue)
        var showSnackbar by remember { mutableStateOf(false) }
        var resetPicker by remember { mutableStateOf(false) }
        val timePickerState = rememberTimePickerState(
            initialHour = if (resetPicker) initialHour else TimeDisplayUtility.convertFromSec(timerValue).first,
            initialMinute = if (resetPicker) initialMinute else TimeDisplayUtility.convertFromSec(timerValue).second,
            is24Hour = true
        )
        var showDialog by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)) {
            Text(
                text = "$timerName Timer",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = formatTime(timerValue),
                modifier = Modifier.padding(end = 8.dp)

            )
            Button(onClick = { showDialog = true }) {
                Text("Change")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Set $timerName Timer") },
                text = {
                    TimePicker(
                        state = timePickerState
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                        val timerVal = TimeDisplayUtility.convertToSec(
                            timePickerState.hour, timePickerState.minute
                        )
                        Log.d("ConfigUI", "timerVal: $timerVal")
                        validTimerLimit = if (type == "daily")
                            timerVal > SessionTimer.limit!! else DailyTimer.limit!! > timerVal
                        Log.d("ConfigUI",
                            "Daily: ${DailyTimer.limit} Session: ${SessionTimer.limit}")


                        if (validTimerLimit){
                            DataManager.setTimerLimit(type, timerVal)
                            timerValue = timerVal
                        } else {
                            showSnackbar = true
                        }
                        showDialog = false
                            onLimitChange()

                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        if (showSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("Dismiss")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Session timer cannot be longer than Daily Timer.")
            }
        }
    }
}



/**
 * Composable function for previewing the ConfigUI Content.
 */
@Preview(showBackground = true)
@Composable
fun ConfigScreenPreview() {
    ConfigUI.Content(
        onSave = {},
        onCancel = {}
    )
}
