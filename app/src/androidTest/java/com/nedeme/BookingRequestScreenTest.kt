package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.ui.screens.booking.BookingRequestScreen
import com.nedeme.ui.screens.booking.BookingUiState
import com.nedeme.ui.theme.NeDemeTheme
import com.nedeme.util.TestTags
import org.junit.Rule
import org.junit.Test
import java.util.*

class BookingRequestScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bookingScreen_displaysFormElements() {
        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(),
                    category = "plumbing",
                    onSubmit = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Demande de service").assertIsDisplayed()
        composeTestRule.onNodeWithText("Catégorie: Plumbing").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BOOKING_DESCRIPTION).assertIsDisplayed()
        composeTestRule.onNodeWithText("Choisir une date (optionnel)").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BOOKING_SUBMIT).assertIsDisplayed()
    }

    @Test
    fun bookingScreen_submitDisabledWhenDescriptionEmpty() {
        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(),
                    category = "plumbing",
                    onSubmit = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.BOOKING_SUBMIT).assertIsNotEnabled()
    }

    @Test
    fun bookingScreen_submitEnabledWhenDescriptionFilled() {
        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(),
                    category = "plumbing",
                    onSubmit = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.BOOKING_DESCRIPTION)
            .performTextInput("Fuite dans la cuisine")

        composeTestRule.onNodeWithTag(TestTags.BOOKING_SUBMIT).assertIsEnabled()
    }

    @Test
    fun bookingScreen_submitCallsCallbackWithDescription() {
        var submittedDescription = ""

        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(),
                    category = "plumbing",
                    onSubmit = { desc, _ -> submittedDescription = desc },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.BOOKING_DESCRIPTION)
            .performTextInput("Robinet cassé")
        composeTestRule.onNodeWithTag(TestTags.BOOKING_SUBMIT).performClick()

        assert(submittedDescription == "Robinet cassé") {
            "Expected 'Robinet cassé' but got '$submittedDescription'"
        }
    }

    @Test
    fun bookingScreen_showsSuccessState() {
        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(isSuccess = true),
                    category = "plumbing",
                    onSubmit = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.BOOKING_SUCCESS).assertIsDisplayed()
        composeTestRule.onNodeWithText("Le professionnel a été notifié").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retour").assertIsDisplayed()
    }

    @Test
    fun bookingScreen_showsErrorState() {
        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(error = "Erreur réseau"),
                    category = "plumbing",
                    onSubmit = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Erreur réseau").assertIsDisplayed()
    }

    @Test
    fun bookingScreen_submitDisabledWhenLoading() {
        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(isLoading = true),
                    category = "plumbing",
                    onSubmit = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.BOOKING_DESCRIPTION)
            .performTextInput("Something")
        composeTestRule.onNodeWithTag(TestTags.BOOKING_SUBMIT).assertIsNotEnabled()
    }

    @Test
    fun bookingScreen_successHasReturnButton() {
        var navigatedBack = false

        composeTestRule.setContent {
            NeDemeTheme {
                BookingRequestScreen(
                    uiState = BookingUiState(isSuccess = true),
                    category = "electrical",
                    onSubmit = { _, _ -> },
                    onNavigateBack = { navigatedBack = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Retour").performClick()
        assert(navigatedBack) { "Expected navigation back" }
    }
}
