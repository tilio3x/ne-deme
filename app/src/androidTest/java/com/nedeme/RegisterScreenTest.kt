package com.nedeme

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.nedeme.data.model.UserRole
import com.nedeme.ui.screens.auth.AuthUiState
import com.nedeme.ui.screens.auth.RegisterScreen
import com.nedeme.ui.theme.NeDemeTheme
import com.nedeme.util.TestTags
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_displaysAllFields() {
        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, _ -> },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Inscription").assertIsDisplayed()
        composeTestRule.onNodeWithText("Je suis...").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CLIENT_CHIP).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_TRADESPERSON_CHIP).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_NAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PHONE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PASSWORD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).assertIsDisplayed()
    }

    @Test
    fun registerScreen_clientSelectedByDefault() {
        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, _ -> },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_CLIENT_CHIP).assertIsSelected()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_TRADESPERSON_CHIP).assertIsNotSelected()
    }

    @Test
    fun registerScreen_canSwitchToProfessionnelRole() {
        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, _ -> },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_TRADESPERSON_CHIP).performClick()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_TRADESPERSON_CHIP).assertIsSelected()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CLIENT_CHIP).assertIsNotSelected()
    }

    @Test
    fun registerScreen_buttonDisabledWhenFieldsEmpty() {
        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, _ -> },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun registerScreen_buttonEnabledWhenAllFieldsFilled() {
        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, _ -> },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_NAME).performTextInput("Test User")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_EMAIL).performTextInput("test@test.com")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PHONE).performTextInput("771234567")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PASSWORD).performTextInput("password123")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performTextInput("password123")

        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).assertIsEnabled()
    }

    @Test
    fun registerScreen_showsPasswordMismatchError() {
        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, _ -> },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_NAME).performTextInput("Test")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_EMAIL).performTextInput("t@t.com")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PHONE).performTextInput("123")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PASSWORD).performTextInput("password1")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performTextInput("password2")

        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).performClick()

        composeTestRule.onNodeWithText("Les mots de passe ne correspondent pas").assertIsDisplayed()
    }

    @Test
    fun registerScreen_showsShortPasswordError() {
        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, _ -> },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_NAME).performTextInput("Test")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_EMAIL).performTextInput("t@t.com")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PHONE).performTextInput("123")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PASSWORD).performTextInput("abc")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performTextInput("abc")

        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).performClick()

        composeTestRule.onNodeWithText("Le mot de passe doit contenir au moins 6 caractères").assertIsDisplayed()
    }

    @Test
    fun registerScreen_submitsWithCorrectRole() {
        var submittedRole: UserRole? = null

        composeTestRule.setContent {
            NeDemeTheme {
                RegisterScreen(
                    uiState = AuthUiState(),
                    onRegister = { _, _, _, _, role -> submittedRole = role },
                    onNavigateBack = {},
                    onClearError = {}
                )
            }
        }

        // Switch to Professionnel
        composeTestRule.onNodeWithTag(TestTags.REGISTER_TRADESPERSON_CHIP).performClick()

        composeTestRule.onNodeWithTag(TestTags.REGISTER_NAME).performTextInput("Pro User")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_EMAIL).performTextInput("pro@test.com")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PHONE).performTextInput("771234567")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_PASSWORD).performTextInput("password123")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_CONFIRM_PASSWORD).performTextInput("password123")

        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).performScrollTo()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_BUTTON).performClick()

        assert(submittedRole == UserRole.TRADESPERSON) { "Expected TRADESPERSON but got $submittedRole" }
    }
}
