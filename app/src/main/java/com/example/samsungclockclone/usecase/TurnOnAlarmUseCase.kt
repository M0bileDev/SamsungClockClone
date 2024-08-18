package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.utils.AlarmId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class TurnOnAlarmUseCase @Inject constructor(
    private val alarmDao: AlarmDao
) {
    suspend operator fun invoke(
        alarmId: AlarmId,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(alarmId)
            val updatedAlarm = alarm.copy(enable = true)
            alarmDao.updateAlarm(updatedAlarm)
            // TODO: set alarm manager after full db implementation
        }
    }
}