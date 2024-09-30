package com.example.samsungclockclone.framework.preferences

import com.example.samsungclockclone.domain.model.AlarmOrder
import kotlinx.coroutines.flow.Flow

interface AlarmPreferences {

    suspend fun saveAlarmOrder(alarmOrder: AlarmOrder)
    fun collectAlarmOrder(): Flow<AlarmOrder>
}