package com.nedeme.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.nedeme.data.repository.AuthRepository
import com.nedeme.data.repository.BookingRepository
import com.nedeme.data.repository.TradespersonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        messaging: FirebaseMessaging
    ): AuthRepository = AuthRepository(auth, firestore, messaging)

    @Provides
    @Singleton
    fun provideTradespersonRepository(
        firestore: FirebaseFirestore
    ): TradespersonRepository = TradespersonRepository(firestore)

    @Provides
    @Singleton
    fun provideBookingRepository(
        firestore: FirebaseFirestore
    ): BookingRepository = BookingRepository(firestore)
}
