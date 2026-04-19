package com.nedeme

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.nedeme.util.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NeDemeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for new booking requests"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
