package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.framework.scheduler.AlarmScheduler
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class TurnOffAlarmItemUseCase @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(
        alarmId: AlarmId,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(alarmId)
            val updatedAlarm = alarm.copy(enable = false)
            alarmDao.updateAlarm(updatedAlarm)

            alarmManagers
                .map { it.uniqueId }
                .forEach { alarmId ->
                    alarmScheduler.cancel(alarmId)
                }
        }
    }
}