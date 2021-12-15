package me.pavi2410.useCompose.reactQuery

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test
import java.net.URL

class ReactQueryTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Suppress("BlockingMethodInNonBlockingContext")
    @Test
    fun useQueryTest() {
        val someData = """{data: "some data"}"""

        // Start the app
        composeTestRule.setContent {
            Surface(color = MaterialTheme.colors.background) {
                Column {
                    val queryResult by useQuery {
                        fetchSomeData(someData)
                    }

                    when (queryResult) {
                        is QueryResult.Loading -> {
                            Text("Fetching data...", modifier = Modifier.testTag("loading"))
                        }
                        is QueryResult.Error -> {
                            Text("something went wrong...", modifier = Modifier.testTag("error"))
                        }
                        is QueryResult.Content -> {
                            val data = (queryResult as QueryResult.Content<String>).data
                            Text("got data -> $data", modifier = Modifier.testTag("data"))
                        }
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
        composeTestRule.onNodeWithTag("error").assertDoesNotExist()
        composeTestRule.onNodeWithTag("data").assertDoesNotExist()

        Thread.sleep(1000) // wait for data to fetch

        composeTestRule.onNodeWithTag("loading").assertDoesNotExist()
        composeTestRule.onNodeWithTag("data").assertIsDisplayed()
        composeTestRule.onNodeWithTag("data").assertTextEquals("got data -> $someData")
    }

    private suspend fun fetchSomeData(someData: String): String {
        delay(500)
        return someData
    }
}