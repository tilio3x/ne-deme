package com.nedeme.ui.screens.search

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.nedeme.data.model.Tradesperson
import com.nedeme.data.repository.TradespersonRepository
import com.nedeme.util.LocationHelper
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val tradespeople: List<Tradesperson> = emptyList(),
    val category: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val userLocation: GeoPoint? = null,
    val showMapView: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tradespersonRepository: TradespersonRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val category: String = savedStateHandle["category"] ?: ""

    private val _uiState = MutableStateFlow(SearchUiState(category = category))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        fetchLocationAndSearch()
    }

    private fun fetchLocationAndSearch() {
        viewModelScope.launch {
            // Try to get user location first
            val location = LocationHelper.getCurrentLocation(context)
            _uiState.value = _uiState.value.copy(userLocation = location)

            if (category.isNotBlank()) {
                searchByCategory()
            }
        }
    }

    private fun searchByCategory() {
        viewModelScope.launch {
            val location = _uiState.value.userLocation
            tradespersonRepository.searchByCategory(
                category = category,
                userLocation = location
            ).collect { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value.copy(
                        tradespeople = result.data,
                        isLoading = false
                    )
                    is Resource.Error -> _uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleMapView() {
        _uiState.value = _uiState.value.copy(showMapView = !_uiState.value.showMapView)
    }

    fun updateLocation(location: GeoPoint) {
        _uiState.value = _uiState.value.copy(userLocation = location)
        if (category.isNotBlank()) {
            searchByCategory()
        }
    }
}
