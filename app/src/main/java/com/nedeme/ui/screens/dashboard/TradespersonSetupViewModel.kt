package com.nedeme.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.GeoPoint
import com.nedeme.data.model.Tradesperson
import com.nedeme.data.repository.AuthRepository
import com.nedeme.data.repository.TradespersonRepository
import com.nedeme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TradespersonSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tradespersonRepository: TradespersonRepository
) : ViewModel() {

    suspend fun saveProfile(
        categories: List<String>,
        description: String,
        city: String,
        hourlyRate: Double?,
        location: GeoPoint? = null,
        onResult: (Boolean) -> Unit
    ) {
        val user = authRepository.currentUser ?: run {
            onResult(false)
            return
        }

        val userData = authRepository.getCurrentUserData()
        if (userData !is Resource.Success) {
            onResult(false)
            return
        }

        val tradesperson = Tradesperson(
            userId = user.uid,
            displayName = userData.data.displayName,
            categories = categories,
            description = description,
            city = city,
            hourlyRate = hourlyRate,
            phone = userData.data.phone,
            location = location
        )

        when (tradespersonRepository.createOrUpdateProfile(tradesperson)) {
            is Resource.Success -> onResult(true)
            else -> onResult(false)
        }
    }
}
