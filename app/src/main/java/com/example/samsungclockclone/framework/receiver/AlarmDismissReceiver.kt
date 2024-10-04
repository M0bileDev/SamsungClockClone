package com.example.samsungclockclone.framework.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_MANAGER_ID
import com.example.samsungclockclone.usecase.notification.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getLongExtra(ALARM_MANAGER_ID, -1L) ?: -1L
        if (notificationId == -1L) return

        notificationBuilder.cancelAlarmNotification(notificationId)
    }
}