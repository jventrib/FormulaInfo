package com.jventrib.formulainfo.ui

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jventrib.formulainfo.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun testMainScreen() {
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAG")
        waitForNode("Bahrain Grand Prix").performTouchInput {
            swipeDown(300f, 1600f, 500L)
        }
        Thread.sleep(5000)
        waitForNode("Bahrain Grand Prix").performClick()

        // In Bahrain 2022 result page
        waitForNode("Bahrain Grand Prix")
        Espresso.pressBack()

        // Back to schedule
        waitForNode("2022", false).performClick()
        composeTestRule.onNodeWithText("2021").assertExists().performClick()
        Thread.sleep(2000)
        // Select year 2021
        waitForNode("Bahrain Grand Prix").performClick()
        waitForNode("Bahrain Grand Prix").performTouchInput {
            swipeUp(1600f, 900f, 1500L)
        }
        // In Bahrain 2021 result page
        waitForNode("2:Max Verstappen").performClick()
        // In verstappen Bahrain 2021 result page
        Espresso.pressBack()

        // In Bahrain 2021 Result
        waitForNode("Bahrain Grand Prix")
        composeTestRule.onNodeWithTag("standing").assertExists().performClick()
        waitForNode("25 pts")
        Espresso.pressBack()

        waitForNodeFromTag("resultChart").performClick()
        waitForNode("Leader Interval").performClick()
        composeTestRule.onNodeWithText("Position by lap").assertExists().performClick()
        composeTestRule.onNodeWithText("Position by lap").assertExists().performClick()
        composeTestRule.onNodeWithText("Time by lap").assertExists().performClick()
        Espresso.pressBack()
        Espresso.pressBack()

        // In 2021 schedule
        waitForNodeFromTag("standing").performClick()
        waitForNodeFromTag("standingChart").performClick()
        waitForNodeFromTag("standing").performClick()
        waitForNode("395,5")
    }

    private fun waitForNode(text: String, substring: Boolean = true): SemanticsNodeInteraction {
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText(text, substring = substring)
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        return composeTestRule.onNodeWithText(text, substring = substring).assertExists()
    }

    private fun waitForNodeFromTag(tag: String, substring: Boolean = true): SemanticsNodeInteraction {
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag(tag)
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        return composeTestRule.onNodeWithTag(tag).assertExists()
    }
}
