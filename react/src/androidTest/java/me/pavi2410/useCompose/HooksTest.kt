package me.pavi2410.useCompose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class HooksTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun useStateTest() {
        // Start the app
        composeTestRule.setContent {
            Surface(color = MaterialTheme.colors.background) {
                Column {
                    val (count, setCount) = useState(0)

                    Text("You clicked $count times")

                    Button(onClick = { setCount(count + 1) }) {
                        Text("Click me")
                    }
                }
            }
        }

        repeat(5) {
            composeTestRule.onNodeWithText("Click me").performClick()
        }

        composeTestRule.onNodeWithText("You clicked 5 times").assertIsDisplayed()
    }

    @Test
    fun useEffectTest() {
        var onEnterInvoked = 0
        var countUpdateInvoked = 0

        // Start the app
        composeTestRule.setContent {
            Surface(color = MaterialTheme.colors.background) {
                Column {
                    val (count, setCount) = useState(0)

                    useEffect {
                        onEnterInvoked++
                    }

                    useEffect(count) {
                        countUpdateInvoked++
                    }

                    Text("You clicked $count times")

                    Button(onClick = { setCount(count + 1) }) {
                        Text("Click me")
                    }
                }
            }
        }

        assert(onEnterInvoked == 1)

        repeat(5) {
            composeTestRule.onNodeWithText("Click me").performClick()
        }

        assert(onEnterInvoked == 1)
        assert(countUpdateInvoked == 5)
    }

    @Suppress("LocalVariableName")
    @Test
    fun useContextTest() {
        composeTestRule.setContent {
            val MyContext = createContext("")

            Column {
                MyContext.Provider(value = "outer") {
                    val outerContextValue = useContext(MyContext)
                    Text("context value is $outerContextValue", modifier = Modifier.testTag("outer"))

                    MyContext.Provider(value = "inner") {
                        val innerContextValue = useContext(MyContext)
                        Text("context value is $innerContextValue", modifier = Modifier.testTag("inner"))
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("outer").assertTextEquals("context value is outer")
        composeTestRule.onNodeWithTag("inner").assertTextEquals("context value is inner")
    }
}