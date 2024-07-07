package com.teamdelta.screentime.ui.config

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.timer.TimerManager

class ConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataManager.initialize(applicationContext)
        TimerManager().initialize(applicationContext)
    }

    companion object{
        private var IS_CONFIGURED : Boolean = false

        fun setConfig(status :Boolean){
            IS_CONFIGURED = status
        }

        fun getConfig() : Boolean{
            return IS_CONFIGURED
        }
    }
}