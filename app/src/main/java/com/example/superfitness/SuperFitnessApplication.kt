package com.example.superfitness

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.superfitness.data.AppContainer
import com.example.superfitness.data.DefaultAppContainer
import com.example.superfitness.utils.CHANNEL_ID

class SuperFitnessApplication: Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = DefaultAppContainer(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}