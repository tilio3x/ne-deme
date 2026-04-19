package com.nedeme.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedeme.data.model.User
import com.nedeme.data.model.UserRole
import com.nedeme.data.repository.AuthRepository
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            if (authRepository.currentUser != null) {
                when (val result = authRepository.getCurrentUserData()) {
                    is Resource.Success -> _uiState.value = AuthUiState(
                        user = result.data,
                        isLoggedIn = true
                    )
                    is Resource.Error -> _uiState.value = AuthUiState(isLoggedIn = false)
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> _uiState.value = AuthUiState(
                    user = result.data,
                    isLoggedIn = true
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                is Resource.Loading -> {}
            }
        }
    }

    fun register(name: String, email: String, phone: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.register(name, email, phone, password, role)) {
                is Resource.Success -> _uiState.value = AuthUiState(
                    user = result.data,
                    isLoggedIn = true
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                is Resource.Loading -> {}
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
