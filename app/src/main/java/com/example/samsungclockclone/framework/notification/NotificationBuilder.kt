package com.example.samsungclockclone.framework.notification

import com.example.samsungclockclone.domain.`typealias`.NotificationId


interface NotificationBuilder {
    fun createAlarmNotificationChannel()

    fun sendAlarmNotification(id: NotificationId, description: String = "")

    fun cancelAlarmNotification(id: NotificationId)
}