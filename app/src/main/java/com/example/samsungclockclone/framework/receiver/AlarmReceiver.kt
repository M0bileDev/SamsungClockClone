package com.example.samsungclockclone.framework.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.usecase.notification.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmManagerId = intent?.getLongExtra(ALARM_MANAGER_ID, -1L) ?: -1L
        val alarmId = intent?.getLongExtra(ALARM_ID, -1L) ?: -1L
        if (alarmId == -1L || alarmManagerId == -1L) return

        notificationBuilder.sendAlarmNotification(alarmManagerId, alarmId)
    }

    companion object {
        const val ALARM_MANAGER_ID = "ALARM_MANAGER_ID"
        const val ALARM_ID = "ALARM_ID"
    }
}