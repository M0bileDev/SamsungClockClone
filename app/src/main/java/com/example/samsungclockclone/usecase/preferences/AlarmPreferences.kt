package com.example.samsungclockclone.usecase.preferences

import com.example.samsungclockclone.domain.model.AlarmOrder
import kotlinx.coroutines.flow.Flow

interface AlarmPreferences {

    suspend fun saveAlarmOrder(alarmOrder: AlarmOrder)
    fun collectAlarmOrder(): Flow<AlarmOrder>
}