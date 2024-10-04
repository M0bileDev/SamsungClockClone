package com.example.samsungclockclone.usecase.notification

import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId


interface NotificationBuilder {
    fun createAlarmNotificationChannel()

    fun sendAlarmNotification(id: AlarmManagerId, alarmId: AlarmId, description: String = "")

    fun cancelAlarmNotification(id: AlarmManagerId)
}