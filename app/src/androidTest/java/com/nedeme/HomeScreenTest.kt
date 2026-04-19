package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.data.model.ServiceCategory
import com.nedeme.ui.screens.home.HomeScreen
import com.nedeme.ui.screens.home.HomeUiState
import com.nedeme.ui.theme.NeDemeTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCategories = listOf(
        ServiceCategory("plumbing", "Plomberie", "Plumbing", "water_drop", 1),
        ServiceCategory("electrical", "Electricité", "Electrical", "bolt", 2),
        ServiceCategory("gardening", "Jardinage", "Gardening", "yard", 3),
        ServiceCategory("cleaning", "Nettoyage", "Cleaning", "cleaning_services", 4)
    )

    @Test
    fun homeScreen_displaysCategories() {
        composeTestRule.setContent {
            NeDemeTheme {
                HomeScreen(
                    uiState = HomeUiState(categories = testCategories, isLoading = false),
                    onCategoryClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Ne Deme").assertIsDisplayed()
        composeTestRule.onNodeWithText("De quoi avez-vous besoin ?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Plomberie").assertIsDisplayed()
        composeTestRule.onNodeWithText("Electricité").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jardinage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nettoyage").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsLoadingIndicator() {
        composeTestRule.setContent {
            NeDemeTheme {
                HomeScreen(
                    uiState = HomeUiState(isLoading = true),
                    onCategoryClick = {}
                )
            }
        }

        composeTestRule.onNode(hasTestTag("home_category_grid")).assertDoesNotExist()
    }

    @Test
    fun homeScreen_showsErrorMessage() {
        composeTestRule.setContent {
            NeDemeTheme {
                HomeScreen(
                    uiState = HomeUiState(isLoading = false, error = "Network error"),
                    onCategoryClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    }

    @Test
    fun homeScreen_categoryClickCallsCallback() {
        var clickedCategory = ""

        composeTestRule.setContent {
            NeDemeTheme {
                HomeScreen(
                    uiState = HomeUiState(categories = testCategories, isLoading = false),
                    onCategoryClick = { clickedCategory = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Plomberie").performClick()
        assert(clickedCategory == "plumbing") { "Expected plumbing but got $clickedCategory" }
    }
}
