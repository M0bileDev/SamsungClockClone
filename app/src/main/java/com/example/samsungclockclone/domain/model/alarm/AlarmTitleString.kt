package com.example.samsungclockclone.domain.model.alarm

sealed interface AlarmTitleString {
    data object AlarmsOff : AlarmTitleString
    data class NearestAlarm(val alarmMillis: Long, val alarmDifference: AlarmDifference) :
        AlarmTitleString
}