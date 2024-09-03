package com.example.samsungclockclone.usecase

import android.app.AlarmManager
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.scheduler.AlarmScheduler
import com.example.samsungclockclone.domain.utils.AlarmId
import com.example.samsungclockclone.ext.suspendCheckPermission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateAlarmEnableSwitchUseCase @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmScheduler: AlarmScheduler,
    private val alarmManager: AlarmManager
) {


    suspend operator fun invoke(
        alarmId: AlarmId,
        onScheduleCompleted: () -> Unit = {},
        onScheduleDenied: () -> Unit,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(alarmId)
            val enableAlarm = !alarm.enable

            when (enableAlarm) {
                true -> {
                    alarmManager.suspendCheckPermission(
                        this,
                        onPermissionGranted = {
                            //todo unable until branch turn_on_alarm_manager
//                            val idMillisecondsPairs =
//                                alarmManagers.map { it.uniqueId to it.fireTime }
//                            alarmScheduler.schedule(
//                                idMillisecondsPairs,
//                                onScheduleCompleted,
//                                onScheduleDenied
//                            )
//
//                            val updatedAlarm = alarm.copy(enable = enableAlarm)
//                            alarmDao.updateAlarm(updatedAlarm)
                        },
                        onPermissionDenied = onScheduleDenied
                    )
                }

                false -> {
                    alarmManagers
                        .map { it.uniqueId }
                        .forEach { alarmId ->
                            alarmScheduler.cancel(alarmId)
                        }

                    val updatedAlarm = alarm.copy(enable = false)
                    alarmDao.updateAlarm(updatedAlarm)
                }
            }
        }
    }
}