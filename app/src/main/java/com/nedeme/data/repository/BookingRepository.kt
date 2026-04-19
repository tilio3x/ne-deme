package com.nedeme.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nedeme.data.model.BookingRequest
import com.nedeme.data.model.BookingStatus
import com.nedeme.data.model.Review
import com.nedeme.util.Constants
import com.nedeme.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createBooking(booking: BookingRequest): Resource<String> {
        return try {
            val docRef = firestore.collection(Constants.BOOKINGS_COLLECTION).document()
            val bookingWithId = booking.copy(id = docRef.id)
            docRef.set(bookingWithId).await()
            Resource.Success(docRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create booking")
        }
    }

    fun getBookingsForClient(clientId: String): Flow<Resource<List<BookingRequest>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection(Constants.BOOKINGS_COLLECTION)
            .whereEqualTo("clientId", clientId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to load bookings"))
                    return@addSnapshotListener
                }
                val bookings = snapshot?.toObjects(BookingRequest::class.java) ?: emptyList()
                trySend(Resource.Success(bookings))
            }
        awaitClose { listener.remove() }
    }

    fun getBookingsForTradesperson(
        tradespersonId: String,
        status: BookingStatus? = null
    ): Flow<Resource<List<BookingRequest>>> = callbackFlow {
        trySend(Resource.Loading)
        var query: Query = firestore.collection(Constants.BOOKINGS_COLLECTION)
            .whereEqualTo("tradespersonId", tradespersonId)

        if (status != null) {
            query = query.whereEqualTo("status", status.name)
        }

        val listener = query
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to load bookings"))
                    return@addSnapshotListener
                }
                val bookings = snapshot?.toObjects(BookingRequest::class.java) ?: emptyList()
                trySend(Resource.Success(bookings))
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateBookingStatus(
        bookingId: String,
        status: BookingStatus
    ): Resource<Unit> {
        return try {
            firestore.collection(Constants.BOOKINGS_COLLECTION)
                .document(bookingId)
                .update(
                    mapOf(
                        "status" to status.name,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update booking")
        }
    }

    suspend fun submitReview(review: Review): Resource<Unit> {
        return try {
            val docRef = firestore.collection(Constants.REVIEWS_COLLECTION).document()
            val reviewWithId = review.copy(id = docRef.id)
            docRef.set(reviewWithId).await()

            // Update tradesperson's average rating
            updateTradespersonRating(review.tradespersonId)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit review")
        }
    }

    private suspend fun updateTradespersonRating(tradespersonId: String) {
        val reviews = firestore.collection(Constants.REVIEWS_COLLECTION)
            .whereEqualTo("tradespersonId", tradespersonId)
            .get()
            .await()
            .toObjects(Review::class.java)

        if (reviews.isNotEmpty()) {
            val avgRating = reviews.map { it.rating }.average()
            firestore.collection(Constants.TRADESPEOPLE_COLLECTION)
                .document(tradespersonId)
                .update(
                    mapOf(
                        "averageRating" to avgRating,
                        "totalReviews" to reviews.size
                    )
                )
                .await()
        }
    }
}
