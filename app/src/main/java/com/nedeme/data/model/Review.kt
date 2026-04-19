package com.nedeme.data.model

import com.google.firebase.Timestamp

data class Review(
    val id: String = "",
    val bookingId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val tradespersonId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
