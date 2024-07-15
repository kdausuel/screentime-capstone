package com.teamdelta.screentime

import android.content.BroadcastReceiver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper
import androidx.glance.GlanceId
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
import com.teamdelta.screentime.notify.NotificationActivity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


//@RunWith(AndroidJUnit4::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ScreenTimeWidgetUnitTests {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        DataManager.initialize(context)
        DataManager.setConfig(true)
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
        DailyTimer.updateCurrentValue(DailyTimer.limit!!)
        println("Before reset: limit = ${DailyTimer.limit}, currentValue = ${DailyTimer.currentValue}")
        assert(DailyTimer.limit == 28800)
        assert(DailyTimer.currentValue == 28800)
    }

    @Test
    fun testDailyTimerCountdown() {

        DailyTimer.setLimit(3605)
        DailyTimer.updateCurrentValue(DailyTimer.limit!!)
        println("Daily Timer value = ${DailyTimer.currentValue}")
        DailyTimer.isRunning = true
        println("Daily Timer running = ${DailyTimer.isRunning}")
        assert(DailyTimer.isRunning)

        // Initialize the TimerManager
        TimerManager.initialize(context)

        // Mock GlanceId
        val mockGlanceId = mock(GlanceId::class.java)

        // Call updateTimers directly
        for (i in 1..5) {
            runBlocking {
                TimerManager.updateTimers(mockGlanceId)
            }
            // Simulate 1 second delay
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(1, TimeUnit.SECONDS)
        }

        // Check the timer value
        println("Daily Timer running = ${DailyTimer.isRunning}")
        assert(DailyTimer.isRunning)
        println("Daily Timer value = ${DailyTimer.currentValue}")
        assert(DailyTimer.currentValue == 3600)
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
        println("Before reset: limit = ${DailyTimer.limit}, currentValue = ${DailyTimer.currentValue}")
        DailyTimer.reset()
        println("After reset: limit = ${DailyTimer.limit}, currentValue = ${DailyTimer.currentValue}")
        assert(DailyTimer.currentValue == 3600)
    }

    // SessionTimer Tests
    @Test
    fun testSessionTimerInitialization() {
        SessionTimer.setLimit(7200) // 2 hours
        SessionTimer.updateCurrentValue(SessionTimer.limit!!)
        assert(SessionTimer.limit == 7200)
        assert(SessionTimer.currentValue == 7200)
    }

    @Test
    fun testSessionTimerCountdown() {
        SessionTimer.setLimit(3605)
        SessionTimer.updateCurrentValue(DailyTimer.limit!!)
        println("Session Timer value = ${SessionTimer.currentValue}")
        SessionTimer.isRunning = true
        println("Session Timer running = ${SessionTimer.isRunning}")
        assert(SessionTimer.isRunning)

        // Initialize the TimerManager
        TimerManager.initialize(context)

        // Mock GlanceId
        val mockGlanceId = mock(GlanceId::class.java)

        // Call updateTimers directly
        for (i in 1..5) {
            runBlocking {
                TimerManager.updateTimers(mockGlanceId)
            }
            // Simulate 1 second delay
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(1, TimeUnit.SECONDS)
        }

        // Check the timer value
        println("Session Timer value = ${SessionTimer.currentValue}")
        assert(SessionTimer.currentValue == 3600)
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
        println("Before reset: limit = ${SessionTimer.limit}, currentValue = ${SessionTimer.currentValue}")
        SessionTimer.reset()
        println("Before reset: limit = ${SessionTimer.limit}, currentValue = ${SessionTimer.currentValue}")
        assert(SessionTimer.currentValue == 3600)
    }
}


    // TimerManager Tests
    @RunWith(RobolectricTestRunner::class)
    //@RunWith(MockitoJUnitRunner::class)
    class TimerManagerTest {
        @Mock
        private lateinit var mockContext: Context

        @Before
        fun setup() {
            mockContext = ApplicationProvider.getApplicationContext()
            DataManager.initialize(mockContext)
            DataManager.setConfig(true)
            DailyTimer.setLimit(9605)
            DailyTimer.updateCurrentValue(DailyTimer.limit!!)
            DailyTimer.isRunning = true
            SessionTimer.setLimit(8605)
            SessionTimer.updateCurrentValue(SessionTimer.limit!!)
            SessionTimer.isRunning = true
            TimerManager.initialize(mockContext)
        }


        /*@Test
        fun testTimerManagerInitialization() {
            TimerManager.initialize(mockContext)
            // Verify that necessary operations are performed during initialization
            verify(mockContext).registerReceiver(any(), any())
        }*/

        @Test
        fun testTimerManagerPauseResume() {
            TimerManager.pauseTimers(true)
            assert(!DailyTimer.isRunning && !SessionTimer.isRunning)

            TimerManager.pauseTimers(false)
            assert(DailyTimer.isRunning && SessionTimer.isRunning)
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
    }

    // NotificationLauncher Tests
    @RunWith(RobolectricTestRunner::class)
    class NotificationLauncherTest {
        @Mock
        private lateinit var mockContext: Context

        @Before
        fun setup() {
            MockitoAnnotations.openMocks(this)
            mockContext = ApplicationProvider.getApplicationContext()
        }

        @Test
        fun testNotificationLaunch() {
            // Clear any previous intents
            val shadowApplication = ShadowApplication.getInstance()

            // Launch the notification
            NotificationLauncher.launchNotify(mockContext, "daily")

            // Create the expected intent
            val expectedIntent = Intent(mockContext, NotificationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Capture the actual started intent
            val actualIntent = shadowApplication.nextStartedActivity

            // Verify the intent
            assertEquals(expectedIntent.component, actualIntent.component)
            assertEquals(expectedIntent.flags, actualIntent.flags)
        }
    }

    // ScreenStateManager Tests
    @RunWith(RobolectricTestRunner::class)
    class ScreenStateManagerTest {
        @Mock
        private lateinit var mockContext: Context

        @Captor
        private lateinit var intentFilterCaptor: ArgumentCaptor<IntentFilter>

        @Before
        fun setup() {
            MockitoAnnotations.openMocks(this)
        }


        @Test
        fun testScreenStateManagerInitialization() {
            /// Initialize ScreenStateManager with the mock context
            ScreenStateManager.initialize(mockContext)

            // Create an IntentFilter with the expected actions
            val expectedFilter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }

            // Capture the IntentFilter passed to registerReceiver
            verify(mockContext).registerReceiver(any(BroadcastReceiver::class.java), intentFilterCaptor.capture())

            // Assert that the captured IntentFilter has the same actions as the expected one
            val capturedFilter = intentFilterCaptor.value
            assertEquals(expectedFilter.actionsIterator().asSequence().toSet(), capturedFilter.actionsIterator().asSequence().toSet())
        }

        @Test
        fun testScreenStateManagerCleanup() {
            ScreenStateManager.initialize(mockContext)
            ScreenStateManager.cleanup(mockContext)
            // Verify that the broadcast receiver is unregistered
            verify(mockContext).unregisterReceiver(any(BroadcastReceiver::class.java))
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
