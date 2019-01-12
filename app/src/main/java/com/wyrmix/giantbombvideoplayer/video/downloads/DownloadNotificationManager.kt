package com.wyrmix.giantbombvideoplayer.video.downloads

import android.content.Context
import androidx.core.app.NotificationCompat
import com.tonyodev.fetch2.DefaultFetchNotificationManager
import com.tonyodev.fetch2.DownloadNotification

/**
 * TODO customize this so the app doesn't keep vibrating
 *
 * Created by kylea
 *
 * 1/11/2019 at 4:16 PM
 */
class DownloadNotificationManager(context: Context): DefaultFetchNotificationManager(context) {
    override fun updateGroupSummaryNotification(groupId: Int, notificationBuilder: NotificationCompat.Builder, downloadNotifications: List<DownloadNotification>, context: Context): Boolean {
        return super.updateGroupSummaryNotification(groupId, notificationBuilder, downloadNotifications, context)
    }

    override fun updateNotification(notificationBuilder: NotificationCompat.Builder, downloadNotification: DownloadNotification, context: Context) {
        super.updateNotification(notificationBuilder, downloadNotification, context)
    }
}