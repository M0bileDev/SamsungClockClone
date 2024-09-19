package com.example.samsungclockclone.domain.notification

import com.example.samsungclockclone.domain.utils.AlarmId

interface NotificationBuilder {
    fun createAlarmNotificationChannel()

    fun sendAlarmNotification(id: AlarmId, description: String = "")

    fun cancelAlarmNotification(id: AlarmId)
}