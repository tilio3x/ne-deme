package com.nedeme.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedeme.data.model.BookingRequest
import com.nedeme.data.repository.AuthRepository
import com.nedeme.data.repository.BookingRepository
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyBookingsUiState(
    val bookings: List<BookingRequest> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyBookingsUiState())
    val uiState: StateFlow<MyBookingsUiState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            bookingRepository.getBookingsForClient(uid).collect { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(isLoading = true)
                    is Resource.Success -> MyBookingsUiState(bookings = result.data, isLoading = false)
                    is Resource.Error -> MyBookingsUiState(error = result.message, isLoading = false)
                }
            }
        }
    }
}
