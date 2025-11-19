package com.productivitystreak.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.productivitystreak.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NeverZeroAppNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun onboardingFlow_navigatesToDashboard_andOpensAddSheet() {
        composeRule.onNodeWithText("Build habits that last")
            .assertIsDisplayed()

        repeat(3) {
            composeRule.onNodeWithText("Continue").performClick()
        }

        composeRule.onNodeWithText("Continue").performClick()

        composeRule.onNodeWithText("Finish").performClick()

        composeRule.onNodeWithText("Let’s get to work.")
            .assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Add").performClick()

        composeRule.onNodeWithText("What would you like to add?")
            .assertIsDisplayed()
    }
}
