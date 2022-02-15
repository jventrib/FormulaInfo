package com.jventrib.formulainfo.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeDown
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.swipeDown
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
        waitForNode("Monaco Grand Prix").performTouchInput {
            swipeDown(300f, 1600f, 500L)
        }
        waitForNode("Monaco Grand Prix").performClick()

        // In Monaco 2022 result page
        waitForNode("Monaco Grand Prix")
        Espresso.pressBack()

        //Back to schedule
        waitForNode("2022", false).performClick()
        composeTestRule.onNodeWithText("2021").assertExists().performClick()
        Thread.sleep(2000)
        // Select year 2021
        waitForNode("Monaco Grand Prix").performClick()

        // In Monaco 2021 result page
        waitForNode("1:Max Verstappen").performClick()
        // In vertappen Monaco 2021 result page
        Espresso.pressBack()

        // In Monaco 2021 Result
        waitForNode("Monaco Grand Prix")
        composeTestRule.onNodeWithTag("standing").assertExists().performClick()
        waitForNode("105")
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
