package com.nedeme.ui.screens.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.nedeme.data.model.BookingRequest
import com.nedeme.data.model.BookingStatus
import com.nedeme.data.repository.AuthRepository
import com.nedeme.data.repository.BookingRepository
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class BookingUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val tradespersonId: String = savedStateHandle["tradespersonId"] ?: ""
    val category: String = savedStateHandle["category"] ?: ""

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun submitBooking(description: String, date: Date?) {
        viewModelScope.launch {
            _uiState.value = BookingUiState(isLoading = true)

            val userData = authRepository.getCurrentUserData()
            if (userData !is Resource.Success) {
                _uiState.value = BookingUiState(error = "Impossible de récupérer vos données")
                return@launch
            }

            val booking = BookingRequest(
                clientId = userData.data.uid,
                clientName = userData.data.displayName,
                tradespersonId = tradespersonId,
                category = category,
                description = description,
                status = BookingStatus.PENDING,
                requestedDate = date?.let { Timestamp(it) }
            )

            when (val result = bookingRepository.createBooking(booking)) {
                is Resource.Success -> _uiState.value = BookingUiState(isSuccess = true)
                is Resource.Error -> _uiState.value = BookingUiState(error = result.message)
                is Resource.Loading -> {}
            }
        }
    }
}
