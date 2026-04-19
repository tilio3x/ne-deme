package com.nedeme.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nedeme.data.model.Review
import com.nedeme.data.model.ServiceCategory
import com.nedeme.data.model.Tradesperson
import com.google.firebase.firestore.GeoPoint
import com.nedeme.util.Constants
import com.nedeme.util.LocationHelper
import com.nedeme.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TradespersonRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getCategories(): Flow<Resource<List<ServiceCategory>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection(Constants.CATEGORIES_COLLECTION)
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to load categories"))
                    return@addSnapshotListener
                }
                val categories = snapshot?.toObjects(ServiceCategory::class.java) ?: emptyList()
                trySend(Resource.Success(categories))
            }
        awaitClose { listener.remove() }
    }

    fun searchByCategory(
        category: String,
        userLocation: GeoPoint? = null,
        radiusKm: Double = 50.0
    ): Flow<Resource<List<Tradesperson>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection(Constants.TRADESPEOPLE_COLLECTION)
            .whereArrayContains("categories", category)
            .whereEqualTo("isAvailable", true)
            .orderBy("isFeatured", Query.Direction.DESCENDING)
            .orderBy("averageRating", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Search failed"))
                    return@addSnapshotListener
                }
                var list = snapshot?.toObjects(Tradesperson::class.java) ?: emptyList()

                // Filter by distance if user location is available
                if (userLocation != null) {
                    list = list.filter { tp ->
                        tp.location?.let {
                            LocationHelper.distanceInKm(userLocation, it) <= radiusKm
                        } ?: true // Include tradespeople without location set
                    }
                }

                trySend(Resource.Success(list))
            }
        awaitClose { listener.remove() }
    }

    suspend fun getTradesperson(userId: String): Resource<Tradesperson> {
        return try {
            val doc = firestore.collection(Constants.TRADESPEOPLE_COLLECTION)
                .document(userId)
                .get()
                .await()
            val tp = doc.toObject(Tradesperson::class.java)
                ?: return Resource.Error("Tradesperson not found")
            Resource.Success(tp)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load profile")
        }
    }

    suspend fun createOrUpdateProfile(tradesperson: Tradesperson): Resource<Unit> {
        return try {
            firestore.collection(Constants.TRADESPEOPLE_COLLECTION)
                .document(tradesperson.userId)
                .set(tradesperson)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save profile")
        }
    }

    suspend fun toggleAvailability(userId: String, available: Boolean): Resource<Unit> {
        return try {
            firestore.collection(Constants.TRADESPEOPLE_COLLECTION)
                .document(userId)
                .update("isAvailable", available)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update availability")
        }
    }

    fun getReviews(tradespersonId: String): Flow<Resource<List<Review>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection(Constants.REVIEWS_COLLECTION)
            .whereEqualTo("tradespersonId", tradespersonId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to load reviews"))
                    return@addSnapshotListener
                }
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(Resource.Success(reviews))
            }
        awaitClose { listener.remove() }
    }
}
