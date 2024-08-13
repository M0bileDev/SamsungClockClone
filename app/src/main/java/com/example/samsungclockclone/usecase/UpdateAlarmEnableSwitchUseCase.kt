package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.scheduler.AlarmId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateAlarmEnableSwitchUseCase @Inject constructor(
    private val alarmDao: AlarmDao
) {


    suspend operator fun invoke(
        alarmId: AlarmId,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(alarmId)
            val updatedAlarm = alarm.copy(enable = !alarm.enable)
            alarmDao.updateAlarm(updatedAlarm)

            // TODO: enable after database full implementation
//            alarmManagers.forEach { alarmManager ->
//                alarmScheduler.cancel(alarmManager.uniqueId)
//            }
        }
    }
}