package com.example.bluetooth_chat

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun activityLaunchesSuccessfully() {
        // This will launch MainActivity if it crashes on startup, the test will fail
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            // Verify that the Activity is not null
            assertNotNull(activity)
        }
    }
}
