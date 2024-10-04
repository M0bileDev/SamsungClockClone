package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.usecase.scheduler.AlarmScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteAllAlarmsUseCase @Inject constructor(
    private val databaseSource: DatabaseSource,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            val allAlarmAndAlarmManagers = databaseSource.getAllAlarmAndAlarmManagers()

            val allAlarmManagers = allAlarmAndAlarmManagers.map { it.alarmMangerEntityList }
            allAlarmManagers
                .flatMap { it.toList() }
                .map { it.uniqueId }
                .forEach { alarmId ->
                    alarmScheduler.cancel(alarmId)
                }

            val allAlarms = allAlarmAndAlarmManagers.map { it.alarmEntity }
            databaseSource.deleteAllAlarms(allAlarms)
        }
    }
}