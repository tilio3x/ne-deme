package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.data.model.BookingRequest
import com.nedeme.data.model.BookingStatus
import com.nedeme.data.model.Tradesperson
import com.nedeme.ui.screens.dashboard.DashboardUiState
import com.nedeme.ui.screens.dashboard.TradespersonDashboardScreen
import com.nedeme.ui.theme.NeDemeTheme
import com.nedeme.util.TestTags
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTradesperson = Tradesperson(
        userId = "tp1",
        displayName = "Moussa",
        isAvailable = true
    )

    private val pendingBooking = BookingRequest(
        id = "b1",
        clientId = "c1",
        clientName = "Amadou Diop",
        tradespersonId = "tp1",
        category = "plumbing",
        description = "Fuite dans la salle de bain",
        status = BookingStatus.PENDING
    )

    private val acceptedBooking = BookingRequest(
        id = "b2",
        clientId = "c2",
        clientName = "Fatou Sow",
        tradespersonId = "tp1",
        category = "electrical",
        description = "Prise murale défectueuse",
        status = BookingStatus.ACCEPTED
    )

    @Test
    fun dashboard_displaysTitle() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(tradesperson = testTradesperson, isLoading = false),
                    onAccept = {}, onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Tableau de bord").assertIsDisplayed()
    }

    @Test
    fun dashboard_showsAvailabilityToggle() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(tradesperson = testTradesperson, isLoading = false),
                    onAccept = {}, onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Disponible").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DASHBOARD_AVAILABILITY_TOGGLE).assertIsDisplayed()
    }

    @Test
    fun dashboard_toggleAvailabilityCallsCallback() {
        var toggled = false

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(tradesperson = testTradesperson, isLoading = false),
                    onAccept = {}, onReject = {}, onComplete = {},
                    onToggleAvailability = { toggled = true }
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.DASHBOARD_AVAILABILITY_TOGGLE).performClick()
        assert(toggled) { "Expected availability toggle callback" }
    }

    @Test
    fun dashboard_showsUnavailableText() {
        val unavailableTp = testTradesperson.copy(isAvailable = false)

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(tradesperson = unavailableTp, isLoading = false),
                    onAccept = {}, onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Indisponible").assertIsDisplayed()
    }

    @Test
    fun dashboard_showsTabs() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(
                        tradesperson = testTradesperson,
                        pendingBookings = listOf(pendingBooking),
                        acceptedBookings = listOf(acceptedBooking),
                        isLoading = false
                    ),
                    onAccept = {}, onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.DASHBOARD_PENDING_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DASHBOARD_ACCEPTED_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithText("En attente (1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Acceptés (1)").assertIsDisplayed()
    }

    @Test
    fun dashboard_pendingTabShowsBookingWithActions() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(
                        tradesperson = testTradesperson,
                        pendingBookings = listOf(pendingBooking),
                        isLoading = false
                    ),
                    onAccept = {}, onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Amadou Diop").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fuite dans la salle de bain").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accepter").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refuser").assertIsDisplayed()
    }

    @Test
    fun dashboard_acceptCallsCallback() {
        var acceptedId = ""

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(
                        tradesperson = testTradesperson,
                        pendingBookings = listOf(pendingBooking),
                        isLoading = false
                    ),
                    onAccept = { acceptedId = it },
                    onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Accepter").performClick()
        assert(acceptedId == "b1") { "Expected b1 but got $acceptedId" }
    }

    @Test
    fun dashboard_rejectCallsCallback() {
        var rejectedId = ""

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(
                        tradesperson = testTradesperson,
                        pendingBookings = listOf(pendingBooking),
                        isLoading = false
                    ),
                    onAccept = {},
                    onReject = { rejectedId = it },
                    onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Refuser").performClick()
        assert(rejectedId == "b1") { "Expected b1 but got $rejectedId" }
    }

    @Test
    fun dashboard_acceptedTabShowsCompleteButton() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(
                        tradesperson = testTradesperson,
                        acceptedBookings = listOf(acceptedBooking),
                        isLoading = false
                    ),
                    onAccept = {}, onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        // Switch to accepted tab
        composeTestRule.onNodeWithTag(TestTags.DASHBOARD_ACCEPTED_TAB).performClick()
        composeTestRule.onNodeWithText("Fatou Sow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Marquer comme terminé").assertIsDisplayed()
    }

    @Test
    fun dashboard_completeCallsCallback() {
        var completedId = ""

        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(
                        tradesperson = testTradesperson,
                        acceptedBookings = listOf(acceptedBooking),
                        isLoading = false
                    ),
                    onAccept = {}, onReject = {},
                    onComplete = { completedId = it },
                    onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.DASHBOARD_ACCEPTED_TAB).performClick()
        composeTestRule.onNodeWithText("Marquer comme terminé").performClick()
        assert(completedId == "b2") { "Expected b2 but got $completedId" }
    }

    @Test
    fun dashboard_emptyStateShowsMessage() {
        composeTestRule.setContent {
            NeDemeTheme {
                TradespersonDashboardScreen(
                    uiState = DashboardUiState(tradesperson = testTradesperson, isLoading = false),
                    onAccept = {}, onReject = {}, onComplete = {}, onToggleAvailability = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Aucune demande").assertIsDisplayed()
    }
}
