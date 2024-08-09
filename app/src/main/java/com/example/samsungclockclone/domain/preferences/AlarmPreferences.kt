package com.example.samsungclockclone.domain.preferences

import kotlinx.coroutines.flow.Flow

interface AlarmPreferences {

    suspend fun saveAlarmOrder(alarmOrder: AlarmOrder)
    suspend fun collectAlarmOrder(): Flow<AlarmOrder>
}

enum class AlarmOrder {
    DEFAULT,
    ALARM_TIME_ORDER,
    CUSTOM_ORDER
}