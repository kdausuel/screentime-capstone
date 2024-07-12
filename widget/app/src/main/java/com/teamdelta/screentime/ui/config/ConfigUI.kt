package com.teamdelta.screentime.ui.config

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
    @Composable
    fun Content(
        onSave : (dailyValue : Int, sessionValue : Int) -> Unit,
        onCancel : () -> Unit
    ){
        // Config UI
        var dailyValue by remember { mutableIntStateOf(10) }
        var sessionValue by remember { mutableIntStateOf(10) }
        Column {
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



}
