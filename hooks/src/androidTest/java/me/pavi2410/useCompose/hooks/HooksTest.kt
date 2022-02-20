package me.pavi2410.useCompose.hooks

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import me.pavi2410.useCompose.hooks.useToggle
import org.junit.Rule
import org.junit.Test

class HooksTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun useToggleTest() {
        // Start the app
        composeTestRule.setContent {
            Surface(color = MaterialTheme.colors.background) {
                Column {
                    val (state, toggle) = useToggle()

                    Text("Switch = ${if (state) "ON" else "OFF"}", Modifier.testTag("status"))

                    Button(onClick = { toggle() }) {
                        Text("Toggle")
                    }
                }
            }
        }

        // initial state
        composeTestRule.onNodeWithTag("status").assertTextEquals("Switch = OFF")

        composeTestRule.onNodeWithText("Toggle").performClick()
        composeTestRule.onNodeWithTag("status").assertTextEquals("Switch = ON")

        composeTestRule.onNodeWithText("Toggle").performClick()
        composeTestRule.onNodeWithTag("status").assertTextEquals("Switch = OFF")
    }
}