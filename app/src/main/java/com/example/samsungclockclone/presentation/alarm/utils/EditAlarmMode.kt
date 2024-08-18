package com.example.samsungclockclone.presentation.alarm.utils

import com.example.samsungclockclone.domain.utils.AlarmId


sealed interface EditAlarmMode {
    data object EditAlarmToolbarAction : EditAlarmMode
    data class EditAlarmItemAction(val alarmId: AlarmId) : EditAlarmMode
}