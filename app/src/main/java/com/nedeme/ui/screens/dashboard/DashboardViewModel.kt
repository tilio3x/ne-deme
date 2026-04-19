package com.nedeme.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedeme.data.model.BookingRequest
import com.nedeme.data.model.BookingStatus
import com.nedeme.data.model.Tradesperson
import com.nedeme.data.repository.AuthRepository
import com.nedeme.data.repository.BookingRepository
import com.nedeme.data.repository.TradespersonRepository
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val tradesperson: Tradesperson? = null,
    val pendingBookings: List<BookingRequest> = emptyList(),
    val acceptedBookings: List<BookingRequest> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val tradespersonRepository: TradespersonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            // Load tradesperson profile
            when (val result = tradespersonRepository.getTradesperson(uid)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    tradesperson = result.data, isLoading = false
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
                is Resource.Loading -> {}
            }
        }

        // Load pending bookings
        viewModelScope.launch {
            bookingRepository.getBookingsForTradesperson(uid, BookingStatus.PENDING).collect { result ->
                if (result is Resource.Success) {
                    _uiState.value = _uiState.value.copy(pendingBookings = result.data)
                }
            }
        }

        // Load accepted bookings
        viewModelScope.launch {
            bookingRepository.getBookingsForTradesperson(uid, BookingStatus.ACCEPTED).collect { result ->
                if (result is Resource.Success) {
                    _uiState.value = _uiState.value.copy(acceptedBookings = result.data)
                }
            }
        }
    }

    fun acceptBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookingId, BookingStatus.ACCEPTED)
        }
    }

    fun rejectBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookingId, BookingStatus.REJECTED)
        }
    }

    fun completeBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookingId, BookingStatus.COMPLETED)
        }
    }

    fun toggleAvailability() {
        val tp = _uiState.value.tradesperson ?: return
        viewModelScope.launch {
            tradespersonRepository.toggleAvailability(tp.userId, !tp.isAvailable)
            _uiState.value = _uiState.value.copy(
                tradesperson = tp.copy(isAvailable = !tp.isAvailable)
            )
        }
    }
}
