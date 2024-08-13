package com.example.samsungclockclone.domain.preferences

import com.example.samsungclockclone.domain.model.AlarmOrder
import kotlinx.coroutines.flow.Flow

interface AlarmPreferences {

    suspend fun saveAlarmOrder(alarmOrder: AlarmOrder)
    suspend fun collectAlarmOrder(): Flow<AlarmOrder>
}