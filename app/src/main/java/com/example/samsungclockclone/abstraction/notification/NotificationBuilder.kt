package com.example.samsungclockclone.abstraction.notification

import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId


interface NotificationBuilder {
    fun createAlarmNotificationChannel()

    fun sendAlarmNotification(alarmManagerId: AlarmManagerId, alarmId: AlarmId, description: String = "")

    fun cancelAlarmNotification(alarmManagerId: AlarmManagerId)
}