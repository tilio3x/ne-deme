package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.data.model.Tradesperson
import com.nedeme.ui.screens.search.SearchResultsScreen
import com.nedeme.ui.screens.search.SearchUiState
import com.nedeme.ui.theme.NeDemeTheme
import com.nedeme.util.TestTags
import org.junit.Rule
import org.junit.Test

class SearchResultsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTradespeople = listOf(
        Tradesperson(
            userId = "tp1", displayName = "Moussa Diallo",
            categories = listOf("plumbing"), city = "Bamako",
            averageRating = 4.8, totalReviews = 20, isFeatured = true,
            hourlyRate = 5000.0, isAvailable = true
        ),
        Tradesperson(
            userId = "tp2", displayName = "Ibrahim Keita",
            categories = listOf("plumbing"), city = "Bamako",
            averageRating = 4.2, totalReviews = 8, isFeatured = false,
            hourlyRate = 3000.0, isAvailable = true
        )
    )

    @Test
    fun searchResults_displaysCategoryTitle() {
        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = testTradespeople,
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Plumbing").assertIsDisplayed()
    }

    @Test
    fun searchResults_displaysTradespeople() {
        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = testTradespeople,
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Moussa Diallo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ibrahim Keita").assertIsDisplayed()
    }

    @Test
    fun searchResults_showsEmptyState() {
        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = emptyList(),
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.SEARCH_EMPTY).assertIsDisplayed()
        composeTestRule.onNodeWithText("Réessayez plus tard").assertIsDisplayed()
    }

    @Test
    fun searchResults_showsErrorState() {
        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        error = "Échec de connexion",
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Échec de connexion").assertIsDisplayed()
    }

    @Test
    fun searchResults_hasMapToggle() {
        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = testTradespeople,
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.SEARCH_MAP_TOGGLE).assertIsDisplayed()
    }

    @Test
    fun searchResults_mapToggleCallsCallback() {
        var toggled = false

        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = testTradespeople,
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = { toggled = true },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.SEARCH_MAP_TOGGLE).performClick()
        assert(toggled) { "Expected map toggle callback" }
    }

    @Test
    fun searchResults_clickTradespersonCallsCallback() {
        var clickedId = ""

        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = testTradespeople,
                        isLoading = false
                    ),
                    onTradespersonClick = { clickedId = it },
                    onToggleMapView = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Moussa Diallo").performClick()
        assert(clickedId == "tp1") { "Expected tp1 but got $clickedId" }
    }

    @Test
    fun searchResults_backNavigates() {
        var navigatedBack = false

        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = testTradespeople,
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = {},
                    onNavigateBack = { navigatedBack = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Retour").performClick()
        assert(navigatedBack) { "Expected back navigation" }
    }

    @Test
    fun searchResults_featuredTradespersonShowsBadge() {
        composeTestRule.setContent {
            NeDemeTheme {
                SearchResultsScreen(
                    uiState = SearchUiState(
                        category = "plumbing",
                        tradespeople = testTradespeople,
                        isLoading = false
                    ),
                    onTradespersonClick = {},
                    onToggleMapView = {},
                    onNavigateBack = {}
                )
            }
        }

        // Featured badge should exist for Moussa (featured) but UI contains it
        composeTestRule.onNodeWithContentDescription("Featured").assertIsDisplayed()
    }
}
