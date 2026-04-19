package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.ui.screens.auth.AuthUiState
import com.nedeme.ui.screens.auth.LoginScreen
import com.nedeme.ui.theme.NeDemeTheme
import com.nedeme.util.TestTags
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysAllElements() {
        composeTestRule.setContent {
            NeDemeTheme {
                LoginScreen(
                    uiState = AuthUiState(),
                    onLogin = { _, _ -> },
                    onNavigateToRegister = {},
                    onClearError = {}
                )
            }
        }

        // Title is displayed
        composeTestRule.onNodeWithTag(TestTags.LOGIN_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText("Ne Deme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trouvez le bon professionnel").assertIsDisplayed()

        // Input fields are displayed
        composeTestRule.onNodeWithTag(TestTags.LOGIN_EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.LOGIN_PASSWORD).assertIsDisplayed()

        // Button is displayed but disabled (fields empty)
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsNotEnabled()

        // Register link is displayed
        composeTestRule.onNodeWithTag(TestTags.LOGIN_REGISTER_LINK).assertIsDisplayed()
    }

    @Test
    fun loginScreen_buttonEnabledWhenFieldsFilled() {
        composeTestRule.setContent {
            NeDemeTheme {
                LoginScreen(
                    uiState = AuthUiState(),
                    onLogin = { _, _ -> },
                    onNavigateToRegister = {},
                    onClearError = {}
                )
            }
        }

        // Type in email
        composeTestRule.onNodeWithTag(TestTags.LOGIN_EMAIL).performTextInput("test@test.com")

        // Button still disabled (password empty)
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsNotEnabled()

        // Type in password
        composeTestRule.onNodeWithTag(TestTags.LOGIN_PASSWORD).performTextInput("password123")

        // Button should now be enabled
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsEnabled()
    }

    @Test
    fun loginScreen_clickLoginCallsCallback() {
        var loginEmail = ""
        var loginPassword = ""

        composeTestRule.setContent {
            NeDemeTheme {
                LoginScreen(
                    uiState = AuthUiState(),
                    onLogin = { email, password ->
                        loginEmail = email
                        loginPassword = password
                    },
                    onNavigateToRegister = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.LOGIN_EMAIL).performTextInput("user@nedeme.com")
        composeTestRule.onNodeWithTag(TestTags.LOGIN_PASSWORD).performTextInput("secret123")
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).performClick()

        assert(loginEmail == "user@nedeme.com") { "Expected user@nedeme.com but got $loginEmail" }
        assert(loginPassword == "secret123") { "Expected secret123 but got $loginPassword" }
    }

    @Test
    fun loginScreen_clickRegisterNavigates() {
        var navigated = false

        composeTestRule.setContent {
            NeDemeTheme {
                LoginScreen(
                    uiState = AuthUiState(),
                    onLogin = { _, _ -> },
                    onNavigateToRegister = { navigated = true },
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.LOGIN_REGISTER_LINK).performClick()
        assert(navigated) { "Expected navigation to register" }
    }

    @Test
    fun loginScreen_showsLoadingState() {
        composeTestRule.setContent {
            NeDemeTheme {
                LoginScreen(
                    uiState = AuthUiState(isLoading = true),
                    onLogin = { _, _ -> },
                    onNavigateToRegister = {},
                    onClearError = {}
                )
            }
        }

        // Button should show loading indicator text should not be visible
        composeTestRule.onNodeWithText("Se connecter").assertDoesNotExist()
    }

    @Test
    fun loginScreen_showsError() {
        composeTestRule.setContent {
            NeDemeTheme {
                LoginScreen(
                    uiState = AuthUiState(error = "Invalid credentials"),
                    onLogin = { _, _ -> },
                    onNavigateToRegister = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.LOGIN_ERROR).assertIsDisplayed()
        composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
    }

    @Test
    fun loginScreen_buttonDisabledWhenLoading() {
        composeTestRule.setContent {
            NeDemeTheme {
                LoginScreen(
                    uiState = AuthUiState(isLoading = true),
                    onLogin = { _, _ -> },
                    onNavigateToRegister = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.LOGIN_EMAIL).performTextInput("a@b.com")
        composeTestRule.onNodeWithTag(TestTags.LOGIN_PASSWORD).performTextInput("123456")
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsNotEnabled()
    }
}
