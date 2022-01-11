package com.bhuvancom.redcatscheduler.util

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bhuvancom.redcatscheduler.R
import com.bhuvancom.redcatscheduler.data.model.NotificationItem.NotificationItemModel


/**
 * @author Bhuvaneshvar
 */
object NotificationUtil {
    @JvmStatic
    fun notify(model: NotificationItemModel, context: Context) {
        val notificationBuild =
            NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(model.title)
                .setContentText(model.subTitle)
                .setVibrate(longArrayOf(10, 20, 30, 10, 20, 30, 10, 20, 30))
                .setChannelId(Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
        val notificationManager = NotificationManagerCompat.from(context)
        val build = notificationBuild.build()
        notificationManager.notify(0, build)
    }

    private const val TAG = "NotificationUtil"
}