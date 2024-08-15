package com.example.samsungclockclone.presentation.alarm.utils

import com.example.samsungclockclone.domain.scheduler.AlarmId

interface AddAlarmMode {
    data object AddAlarmToolbarAction : AddAlarmMode
    data class AddAlarmItemAction(val alarmId: AlarmId) : AddAlarmMode
}