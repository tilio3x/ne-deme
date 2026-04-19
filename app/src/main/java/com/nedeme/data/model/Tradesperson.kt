package com.nedeme.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Tradesperson(
    val userId: String = "",
    val displayName: String = "",
    val categories: List<String> = emptyList(),
    val description: String = "",
    val hourlyRate: Double? = null,
    val photoUrl: String? = null,
    val phone: String = "",
    val location: GeoPoint? = null,
    val city: String = "",
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val completedJobs: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)
