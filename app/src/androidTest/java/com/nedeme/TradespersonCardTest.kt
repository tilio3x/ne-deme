package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.data.model.Tradesperson
import com.nedeme.ui.components.TradespersonCard
import com.nedeme.ui.theme.NeDemeTheme
import org.junit.Rule
import org.junit.Test

class TradespersonCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTradesperson = Tradesperson(
        userId = "tp1",
        displayName = "Moussa Diallo",
        categories = listOf("plumbing"),
        description = "Expert plumber",
        hourlyRate = 5000.0,
        city = "Bamako",
        isAvailable = true,
        isFeatured = false,
        averageRating = 4.5,
        totalReviews = 12
    )

    @Test
    fun tradespersonCard_displaysName() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = testTradesperson,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Moussa Diallo").assertIsDisplayed()
    }

    @Test
    fun tradespersonCard_displaysRating() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = testTradesperson,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("4.5").assertIsDisplayed()
        composeTestRule.onNodeWithText("(12 avis)", substring = true).assertIsDisplayed()
    }

    @Test
    fun tradespersonCard_displaysHourlyRate() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = testTradesperson,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("5000 FCFA/h").assertIsDisplayed()
    }

    @Test
    fun tradespersonCard_displaysCity() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = testTradesperson,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Bamako", substring = true).assertIsDisplayed()
    }

    @Test
    fun tradespersonCard_displaysDistanceWhenProvided() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = testTradesperson,
                    onClick = {},
                    distanceKm = 3.2
                )
            }
        }

        composeTestRule.onNodeWithText("3.2 km", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Bamako", substring = true).assertIsDisplayed()
    }

    @Test
    fun tradespersonCard_displaysFeaturedBadge() {
        val featuredTp = testTradesperson.copy(isFeatured = true)

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = featuredTp,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Featured").assertIsDisplayed()
    }

    @Test
    fun tradespersonCard_noFeaturedBadgeForRegular() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = testTradesperson,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Featured").assertDoesNotExist()
    }

    @Test
    fun tradespersonCard_clickCallsCallback() {
        var clicked = false

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = testTradesperson,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Moussa Diallo").performClick()
        assert(clicked) { "Expected click callback to be called" }
    }

    @Test
    fun tradespersonCard_hidesRateWhenNull() {
        val noRateTp = testTradesperson.copy(hourlyRate = null)

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonCard(
                    tradesperson = noRateTp,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("FCFA/h", substring = true).assertDoesNotExist()
    }
}
