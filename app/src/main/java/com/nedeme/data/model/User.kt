package com.nedeme.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CLIENT,
    val photoUrl: String? = null,
    val fcmToken: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val location: GeoPoint? = null
)

enum class UserRole {
    CLIENT,
    TRADESPERSON
}
