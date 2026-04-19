package com.nedeme.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedeme.data.model.Tradesperson
import com.nedeme.data.repository.TradespersonRepository
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val tradespeople: List<Tradesperson> = emptyList(),
    val category: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tradespersonRepository: TradespersonRepository
) : ViewModel() {

    private val category: String = savedStateHandle["category"] ?: ""

    private val _uiState = MutableStateFlow(SearchUiState(category = category))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        if (category.isNotBlank()) {
            searchByCategory(category)
        }
    }

    private fun searchByCategory(category: String) {
        viewModelScope.launch {
            tradespersonRepository.searchByCategory(category).collect { result ->
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
}
