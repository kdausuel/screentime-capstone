package com.teamdelta.screentime.notify

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity(){
    companion object {
        const val EXTRA_NOTIFY_TYPE = "extra_notify_type"
    }

    /**
     * Called when the activity is starting.
     *
     * Creates and displays an AlertDialog as an overlay.
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized after
     * previously being shut down.
     */
    override fun onCreate(savedInstanceState: Bundle?){
        Log.d("NotificationSys", "onCreate launched")
        Log.d("NotificationSys",
            "Overlay Code: ${Settings.ACTION_MANAGE_OVERLAY_PERMISSION}")

        val notifyType = intent.getStringExtra(EXTRA_NOTIFY_TYPE) ?: "timer"

        runOnUiThread {
            super.onCreate(savedInstanceState)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            val alert = AlertDialog.Builder(this)
                .setTitle("ScreenTime")
                .setMessage(
                    "You have reached your $notifyType limit. Please consider " +
                            "taking a break from your phone for a bit!"
                )
                .setPositiveButton("Ok") { _, _ ->
                    Log.d("NotificationSys", "Notification Cleared")
                    finish()
                }
                .setOnDismissListener { finish() }
                .create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()

        }

    }
}
object NotificationLauncher {
    fun launchNotify(context: Context, timerType: String) {
        val intent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(NotificationActivity.EXTRA_NOTIFY_TYPE, timerType)
        }
        context.startActivity(intent)
    }
}
// XML Modifications:
// Added NotificationActivity to AndroidManifest.xml:
//    - Set android:theme="@android:style/Theme.Translucent.NoTitleBar" for transparency
//    - Set android:exported="false"
