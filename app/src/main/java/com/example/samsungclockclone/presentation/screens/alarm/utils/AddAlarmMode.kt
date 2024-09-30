package com.example.samsungclockclone.presentation.screens.alarm.utils

import com.example.samsungclockclone.domain.utils.AlarmId


interface AddAlarmMode {
    data object AddAlarmToolbarAction : AddAlarmMode
    data class AddAlarmItemAction(val alarmId: AlarmId) : AddAlarmMode
}