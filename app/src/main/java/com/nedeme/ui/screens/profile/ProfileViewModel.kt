package com.nedeme.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedeme.data.model.Review
import com.nedeme.data.model.Tradesperson
import com.nedeme.data.repository.TradespersonRepository
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val tradesperson: Tradesperson? = null,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tradespersonRepository: TradespersonRepository
) : ViewModel() {

    private val tradespersonId: String = savedStateHandle["tradespersonId"] ?: ""

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        loadReviews()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            when (val result = tradespersonRepository.getTradesperson(tradespersonId)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    tradesperson = result.data,
                    isLoading = false
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message,
                    isLoading = false
                )
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            tradespersonRepository.getReviews(tradespersonId).collect { result ->
                if (result is Resource.Success) {
                    _uiState.value = _uiState.value.copy(reviews = result.data)
                }
            }
        }
    }
}
