package com.example.samsungclockclone.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.domain.notification.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getLongExtra(NOTIFICATION_ID, -1L) ?: -1L
        if (notificationId == -1L) return

        notificationBuilder.cancelAlarmNotification(notificationId)
    }

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
    }

}