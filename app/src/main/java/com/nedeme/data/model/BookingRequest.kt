package com.nedeme.data.model

import com.google.firebase.Timestamp

data class BookingRequest(
    val id: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val tradespersonId: String = "",
    val tradespersonName: String = "",
    val category: String = "",
    val description: String = "",
    val status: BookingStatus = BookingStatus.PENDING,
    val requestedDate: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

enum class BookingStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED,
    CANCELLED
}
