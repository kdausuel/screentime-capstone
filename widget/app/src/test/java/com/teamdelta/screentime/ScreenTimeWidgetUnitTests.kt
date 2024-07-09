package com.teamdelta.screentime

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import com.teamdelta.screentime.data.DataManager
import com.teamdelta.screentime.timer.DailyTimer
import com.teamdelta.screentime.timer.SessionTimer
import com.teamdelta.screentime.timer.TimerManager
import com.teamdelta.screentime.ui.widget.ScreenTimeGlanceWidget
import com.teamdelta.screentime.notify.NotificationLauncher
import com.teamdelta.screentime.receiver.ScreenStateManager
import com.teamdelta.screentime.worker.DailyResetWorker
import kotlinx.coroutines.runBlocking
import androidx.work.testing.TestListenableWorkerBuilder
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue


@RunWith(AndroidJUnit4::class)
class ScreenTimeWidgetUnitTests {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        DataManager.initialize(context)
    }

    // DataManager Tests
    @Test
    fun testDataManagerInitialization() {
        assert(DataManager.isInitialized())
    }

    @Test
    fun testDataManagerSetAndGetConfig() {
        DataManager.setConfig(true)
        assert(DataManager.getConfig() == true)
    }

    @Test
    fun testTimerLimitSettingAndRetrieval() {
        // Test Daily Timer
        DataManager.setTimerLimit("daily", 21600) // 6 hours in seconds
        assertEquals(21600, DataManager.getTimerLimit("daily"))

        // Test Session Timer
        DataManager.setTimerLimit("session", 7200) // 2 hours in seconds
        assertEquals(7200, DataManager.getTimerLimit("session"))

        // Test current value setting and retrieval
        DataManager.setTimerCurrentValue("daily", 10800) // 3 hours in seconds
        assertEquals(10800, DataManager.getTimerCurrentValue("daily"))

        // Test running state
        DataManager.setTimerRunning("session", true)
        DataManager.isTimerRunning("session")?.let { assertTrue(it) }
    }

    // DailyTimer Tests
    @Test
    fun testDailyTimerInitialization() {
        DailyTimer.setLimit(28800) // 8 hours
        assert(DailyTimer.limit == 28800)
        assert(DailyTimer.currentValue == 28800)
    }

    @Test
    fun testDailyTimerCountdown() {
        DailyTimer.setLimit(3600)
        DailyTimer.updateCurrentValue(3540)
        assert(DailyTimer.currentValue == 3540)
    }

    @Test
    fun testDailyTimerLimitReached() {
        DailyTimer.setLimit(3600)
        DailyTimer.updateCurrentValue(0)
        assert(DailyTimer.isLimitReached())
    }

    @Test
    fun testDailyTimerReset() {
        DailyTimer.setLimit(3600)
        DailyTimer.updateCurrentValue(1800)
        DailyTimer.reset()
        assert(DailyTimer.currentValue == 3600)
    }

    // SessionTimer Tests
    @Test
    fun testSessionTimerInitialization() {
        SessionTimer.setLimit(7200) // 2 hours
        assert(SessionTimer.limit == 7200)
        assert(SessionTimer.currentValue == 7200)
    }

    @Test
    fun testSessionTimerCountdown() {
        SessionTimer.setLimit(3600)
        SessionTimer.updateCurrentValue(3300)
        assert(SessionTimer.currentValue == 3300)
    }

    @Test
    fun testSessionTimerLimitReached() {
        SessionTimer.setLimit(3600)
        SessionTimer.updateCurrentValue(0)
        assert(SessionTimer.isLimitReached())
    }

    @Test
    fun testSessionTimerReset() {
        SessionTimer.setLimit(3600)
        SessionTimer.updateCurrentValue(1800)
        SessionTimer.reset()
        assert(SessionTimer.currentValue == 3600)
    }

    // TimerManager Tests
    @RunWith(MockitoJUnitRunner::class)
    class TimerManagerTest {
        @Mock
        private lateinit var mockContext: Context

        @Test
        fun testTimerManagerInitialization() {
            TimerManager.initialize(mockContext)
            // Verify that necessary operations are performed during initialization
            verify(mockContext).registerReceiver(any(), any())
        }

        @Test
        fun testTimerManagerPauseResume() {
            TimerManager.pauseTimers(true)
            assert(!DailyTimer.isRunning && !SessionTimer.isRunning)

            TimerManager.pauseTimers(false)
            assert(DailyTimer.isRunning && SessionTimer.isRunning)
        }
    }


    // ScreenTimeGlanceWidget Tests
    @Test
    fun testWidgetUpdate() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Set some initial values
        DailyTimer.updateCurrentValue(3600) // 1 hour
        SessionTimer.updateCurrentValue(1800) // 30 minutes

        // Update the widget
        ScreenTimeGlanceWidget.updateWidget(context)

        // Since we can't directly access the widget state, verify
        // that the DataManager has the correct values
        assertEquals(3600, DataManager.getTimerCurrentValue("daily"))
        assertEquals(1800, DataManager.getTimerCurrentValue("session"))
    }

    // NotificationLauncher Tests
    @RunWith(MockitoJUnitRunner::class)
    class NotificationLauncherTest {
        @Mock
        private lateinit var mockContext: Context

        @Test
        fun testNotificationLaunch() {
            NotificationLauncher.launchNotify(mockContext, "daily")
            // Verify that the correct intent is started
            verify(mockContext).startActivity(any())
        }
    }

    // ScreenStateManager Tests
    @RunWith(MockitoJUnitRunner::class)
    class ScreenStateManagerTest {
        @Mock
        private lateinit var mockContext: Context

        @Test
        fun testScreenStateManagerInitialization() {
            ScreenStateManager.initialize(mockContext)
            // Verify that the broadcast receiver is registered
            verify(mockContext).registerReceiver(any(), any(), Context.RECEIVER_NOT_EXPORTED)
        }

        @Test
        fun testScreenStateManagerCleanup() {
            ScreenStateManager.cleanup(mockContext)
            // Verify that the broadcast receiver is unregistered
            verify(mockContext).unregisterReceiver(any())
        }
    }

    // DailyResetWorker Tests
    @RunWith(AndroidJUnit4::class)
    class DailyResetWorkerTest {
        private lateinit var context: android.content.Context

        @Before
        fun setup() {
            context = ApplicationProvider.getApplicationContext()
        }

        @Test
        fun testDailyResetWorkerExecution() = runBlocking {
            // Set a known value before reset
            DailyTimer.setLimit(3600) // 1 hour
            DailyTimer.updateCurrentValue(1800) // 30 minutes

            val worker = TestListenableWorkerBuilder<DailyResetWorker>(context).build()

            val result = worker.doWork()
            assert(result is ListenableWorker.Result.Success)

            // Verify that the daily timer has been reset
            assert(DailyTimer.currentValue == DailyTimer.limit)
        }
    }
}