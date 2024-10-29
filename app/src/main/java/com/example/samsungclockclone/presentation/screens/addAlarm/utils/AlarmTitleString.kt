package com.example.samsungclockclone.presentation.screens.addAlarm.utils

import com.example.samsungclockclone.domain.model.alarm.AlarmDifference

sealed interface AlarmTitleString {
    data object AlarmsOff : AlarmTitleString
    data class NearestAlarm(val alarmMillis: Long, val alarmDifference: AlarmDifference) :
        AlarmTitleString
}