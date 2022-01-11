package com.bhuvancom.redcatscheduler

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.bhuvancom.redcatscheduler.util.Constants
import dagger.hilt.android.HiltAndroidApp

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   11:12 PM
Project Redcat Scheduler
 */
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = Constants.CHANNEL_DESCRIPTION
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}