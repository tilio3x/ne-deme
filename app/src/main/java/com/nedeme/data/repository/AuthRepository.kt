package com.nedeme.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.nedeme.data.model.User
import com.nedeme.data.model.UserRole
import com.nedeme.util.Constants
import com.nedeme.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole
    ): Resource<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Resource.Error("Registration failed")
            val token = messaging.token.await()

            val user = User(
                uid = uid,
                displayName = name,
                email = email,
                phone = phone,
                role = role,
                fcmToken = token
            )

            firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .set(user)
                .await()

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Resource.Error("Login failed")

            // Update FCM token on login
            val token = messaging.token.await()
            firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .update("fcmToken", token)
                .await()

            val doc = firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            val user = doc.toObject(User::class.java) ?: return Resource.Error("User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun getCurrentUserData(): Resource<User> {
        val uid = currentUser?.uid ?: return Resource.Error("Not logged in")
        return try {
            val doc = firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            val user = doc.toObject(User::class.java) ?: return Resource.Error("User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user data")
        }
    }

    fun logout() {
        auth.signOut()
    }
}
