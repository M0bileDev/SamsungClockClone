package com.example.samsungclockclone.presentation.screens.alarm.utils

import com.example.samsungclockclone.domain.`typealias`.AlarmId


sealed interface EditAlarmMode {
    data object EditAlarmToolbarAction : EditAlarmMode
    data class EditAlarmItemAction(val alarmId: AlarmId) : EditAlarmMode
}