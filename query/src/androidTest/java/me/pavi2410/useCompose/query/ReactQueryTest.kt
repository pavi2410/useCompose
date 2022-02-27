package me.pavi2410.useCompose.query

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test

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
                        is QueryState.Loading -> {
                            Text("Fetching data...", modifier = Modifier.testTag("loading"))
                        }
                        is QueryState.Error -> {
                            Text("something went wrong...", modifier = Modifier.testTag("error"))
                        }
                        is QueryState.Content -> {
                            val data = (queryResult as QueryState.Content<String>).data
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

    @Test
    fun useMutationTest() {
        // Start the app
        composeTestRule.setContent {
            Surface(color = MaterialTheme.colors.background) {
                Column {
                    var token by remember { mutableStateOf("") }

                    val loginMutation = useMutation { (username, password) ->
                        doLogin(username, password)
                    }

                    Button(
                        modifier = Modifier.testTag("login_button"),
                        onClick = {
                            // todo: is this blocking the main thread?
                            // todo: this makes me think I need a mutateAsync too...
                            loginMutation.mutate("pavi2410", "secretpw123") {
                                token = it
                            }
                        }
                    ) {
                        Text("Login")
                    }

                    Text(
                        if (token.isEmpty()) "Please login" else "Welcome! token = $token",
                        modifier = Modifier.testTag("status")
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("login_button").performClick()
        composeTestRule.onNodeWithTag("status").assertTextEquals("Please login")

        Thread.sleep(1000) // wait for data to fetch

        // verify that mutation is called
        composeTestRule.onNodeWithTag("status").assertTextEquals("Welcome! token = secret_token")
    }

    private suspend fun fetchSomeData(someData: String): String {
        delay(500)
        return someData
    }

    private suspend fun doLogin(username: String, password: String): String {
        delay(5)
        return "secret_token"
    }
}