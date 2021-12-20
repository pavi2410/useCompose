package me.pavi2410.useCompose.network

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test


class NetworkTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun useConnectionStatusTest() {
        // Start the app
        composeTestRule.setContent {
            Surface(color = MaterialTheme.colors.background) {
                Column {
                    val isConnected = useConnectionStatus()

                    Text(
                        if (isConnected) "Connected" else "Disconnected",
                        modifier = Modifier.testTag("status")
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("status").assertIsDisplayed()

        // turn on airplane mode
        setAirplaneMode(true)
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("status").assertTextEquals("Disconnected")

        // turn off airplane mode
        setAirplaneMode(false)
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("status").assertTextEquals("Connected")
    }
}

private fun setAirplaneMode(enable: Boolean) {
    if ((if (enable) 1 else 0) == Settings.System.getInt(
            getInstrumentation().context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        )
    ) {
        return
    }
    val device = UiDevice.getInstance(getInstrumentation())
    device.openQuickSettings()
    // Find the text of your language
    val description = By.desc("Airplane mode")
    // Need to wait for the button, as the opening of quick settings is animated.
    device.wait(Until.hasObject(description), 500)
    device.findObject(description).click()
    getInstrumentation().context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
}