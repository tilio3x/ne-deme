package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.data.model.Review
import com.nedeme.data.model.Tradesperson
import com.nedeme.ui.screens.profile.ProfileUiState
import com.nedeme.ui.screens.profile.TradespersonProfileScreen
import com.nedeme.ui.theme.NeDemeTheme
import org.junit.Rule
import org.junit.Test

class TradespersonProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTradesperson = Tradesperson(
        userId = "tp1",
        displayName = "Moussa Diallo",
        categories = listOf("plumbing", "electrical"),
        description = "Expert plombier avec 10 ans d'expérience",
        hourlyRate = 5000.0,
        city = "Bamako",
        isAvailable = true,
        isFeatured = true,
        averageRating = 4.7,
        totalReviews = 15,
        completedJobs = 42
    )

    private val testReviews = listOf(
        Review(
            id = "r1", clientName = "Amadou",
            tradespersonId = "tp1", rating = 5,
            comment = "Excellent travail"
        ),
        Review(
            id = "r2", clientName = "Fatou",
            tradespersonId = "tp1", rating = 4,
            comment = "Très professionnel"
        )
    )

    @Test
    fun profileScreen_displaysName() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(
                        tradesperson = testTradesperson,
                        isLoading = false
                    ),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Moussa Diallo").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysCity() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(tradesperson = testTradesperson, isLoading = false),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Bamako").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysRatingAndStats() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(tradesperson = testTradesperson, isLoading = false),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("4.7").assertIsDisplayed()
        composeTestRule.onNodeWithText("(15 avis)", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("42 travaux", substring = true).assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysHourlyRate() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(tradesperson = testTradesperson, isLoading = false),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("5000 FCFA/h").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysDescription() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(tradesperson = testTradesperson, isLoading = false),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("À propos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Expert plombier avec 10 ans d'expérience").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysCategories() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(tradesperson = testTradesperson, isLoading = false),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Plumbing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Electrical").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysFeaturedBadge() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(tradesperson = testTradesperson, isLoading = false),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Premium").assertIsDisplayed()
    }

    @Test
    fun profileScreen_requestButtonCallsCallback() {
        var requestedTpId = ""
        var requestedCategory = ""

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(tradesperson = testTradesperson, isLoading = false),
                    onRequestService = { id, cat ->
                        requestedTpId = id
                        requestedCategory = cat
                    },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Demander un service").performScrollTo().performClick()
        assert(requestedTpId == "tp1") { "Expected tp1 but got $requestedTpId" }
        assert(requestedCategory == "plumbing") { "Expected plumbing but got $requestedCategory" }
    }

    @Test
    fun profileScreen_displaysReviews() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(
                        tradesperson = testTradesperson,
                        reviews = testReviews,
                        isLoading = false
                    ),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Avis (2)").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Excellent travail").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Très professionnel").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun profileScreen_showsLoadingState() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(isLoading = true),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Moussa Diallo").assertDoesNotExist()
    }

    @Test
    fun profileScreen_showsErrorState() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonProfileScreen(
                    uiState = ProfileUiState(
                        tradesperson = null,
                        isLoading = false,
                        error = "Profil introuvable"
                    ),
                    onRequestService = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Profil introuvable").assertIsDisplayed()
    }
}
