package com.example.samsungclockclone.domain.notification

import com.example.samsungclockclone.domain.utils.NotificationId


interface NotificationBuilder {
    fun createAlarmNotificationChannel()

    fun sendAlarmNotification(id: NotificationId, description: String = "")

    fun cancelAlarmNotification(id: NotificationId)
}