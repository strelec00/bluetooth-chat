package com.example.bluetooth_chat

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.bluetooth_chat.ui.viewmodel.AppPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppPreferencesTest {

    private val context by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Before
    fun clearPreferences() {
        // Make sure we start from a clean slate each time
        AppPreferences.resetFirstLaunch(context)
    }

    @Test
    fun resetFirstLaunch_reportsTrueInitially() {
        // After reset, isFirstLaunch should return true
        assertTrue(
            "After resetFirstLaunch, isFirstLaunch(...) must be true",
            AppPreferences.isFirstLaunch(context)
        )
    }

    @Test
    fun setFirstLaunchComplete_thenIsFirstLaunchIsFalse() {
        // First, ensure reset had set it to true
        assertTrue(AppPreferences.isFirstLaunch(context))

        // Mark first‐launch as complete
        AppPreferences.setFirstLaunchComplete(context)

        // Now isFirstLaunch should return false
        assertFalse(
            "After setFirstLaunchComplete, isFirstLaunch(...) must be false",
            AppPreferences.isFirstLaunch(context)
        )
    }

    @Test
    fun resetAfterSetFirstLaunchComplete_revertsToTrue() {
        // Mark first‐launch as complete
        AppPreferences.setFirstLaunchComplete(context)
        assertFalse(AppPreferences.isFirstLaunch(context))

        // Reset again
        AppPreferences.resetFirstLaunch(context)
        assertTrue(
            "After resetFirstLaunch() is called again, isFirstLaunch(...) must be true",
            AppPreferences.isFirstLaunch(context)
        )
    }
}
