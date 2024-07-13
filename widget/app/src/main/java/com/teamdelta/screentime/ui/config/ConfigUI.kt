package com.teamdelta.screentime.ui.config

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.timer.DailyTimer
import com.teamdelta.screentime.timer.SessionTimer
import com.teamdelta.screentime.ui.TimeDisplayUtility

/**
 * Object containing UI components and functions for the Configuration activity.
 * Provides the UI for configuring the Screen Time widget.
 */
object ConfigUI {

    /**
     * Composable function for displaying the configuration UI.
     *
     * @param onConfirm Callback function to handle saving the configuration.
     * @param onDismiss Callback function to handle canceling the configuration.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content(
        onSave : (dailyValue : Int, sessionValue : Int) -> Unit,
        onCancel : () -> Unit
    ){
        // Config UI
        var dailyValue by remember { mutableIntStateOf(10) }
        var sessionValue by remember { mutableIntStateOf(10) }


        Column {
            TimerInterface("session")
            TextField(
                // Daily Timer
                value = dailyValue.toString(),
                onValueChange = {
                    dailyValue = it.toIntOrNull() ?: 10
                },
                label = { Text("Daily Timer") }
            )
            TextField(
                // Session Timer
                value = sessionValue.toString(),
                onValueChange = {
                    sessionValue = it.toIntOrNull() ?: 10
                },
                label = { Text("Session Timer") }
            )
            Button(
                onClick = {
                    onSave(dailyValue, sessionValue)
                }
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


    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun TimerInterface(type : String) {
        //val storedTime: Int? = if (type == "daily") DailyTimer.limit else SessionTimer.limit
        //val (initialHour, initialMinute, _) = TimeDisplayUtility.convertFromSec(storedTime!!)
        val timePickerState = rememberTimePickerState(
            initialHour = 0,
            initialMinute = 0,
            is24Hour = true
        )
        var showDialog by remember { mutableStateOf(false) }

        Button(onClick = { showDialog = true }) {
            Text("Set $type Timer: ${
                DataManager.getTimerLimit(type)
                    ?.let { TimeDisplayUtility.formatTime(it) }
            }"
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Select Time") },
                text = {
                    TimePicker(state = timePickerState)
                },
                confirmButton = {
                    TextButton(onClick = {
                        //timePickerState.hour, timePickerState.minute
                        val timerVal = TimeDisplayUtility.convertToSec(
                            timePickerState.hour, timePickerState.minute
                        )
                        //DataManager.setTimerLimit(type, timerVal)
                        showDialog = false
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
    }
}

//UI Preview
@Preview(showBackground = true)
@Composable
fun ConfigScreenPreview() {
    ConfigUI.Content(
        onSave = {_,_ -> },
        onCancel = {}
    )
}

//Labeled Timer box

