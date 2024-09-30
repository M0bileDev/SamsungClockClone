package com.example.samsungclockclone.usecase

import android.app.AlarmManager
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.domain.ext.toAlarmRepeat
import com.example.samsungclockclone.framework.scheduler.AlarmScheduler
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.model.DayOfWeek
import com.example.samsungclockclone.framework.ext.suspendCheckPermission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateAlarmUseCase @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmScheduler: AlarmScheduler,
    private val alarmManager: AlarmManager
) {

    suspend operator fun invoke(
        alarmId: AlarmId,
        alarmMode: AlarmMode,
        alarmMillisecondsList: List<Long>,
        selectedDaysOfWeek: List<DayOfWeek>,
        onScheduleCompleted: () -> Unit = {},
        onScheduleDenied: () -> Unit,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            alarmManager.suspendCheckPermission(
                this,
                onPermissionGranted = {

                    alarmDao.updateAlarmMode(alarmId, alarmMode)

                    val alarmManagers = alarmDao.getAlarmManagersById(alarmId)
                    alarmManagers.forEach { alarmManager ->
                        alarmScheduler.cancel(alarmManager.uniqueId)
                    }

                    alarmDao.deleteAlarmManagerById(alarmId)

                    val alarmRepeat = alarmMode.toAlarmRepeat()
                    val entities: List<AlarmManagerEntity> =
                        if (selectedDaysOfWeek.isNotEmpty()) {

                            val zipped = alarmMillisecondsList.zip(selectedDaysOfWeek)
                            zipped.map {
                                val (milliseconds, day) = it
                                AlarmManagerEntity(
                                    parentId = alarmId,
                                    fireTime = milliseconds,
                                    repeat = alarmRepeat,
                                    dayOfWeek = day
                                )

                            }

                        } else {
                            alarmMillisecondsList.map { milliseconds ->
                                AlarmManagerEntity(
                                    parentId = alarmId,
                                    fireTime = milliseconds,
                                    repeat = alarmRepeat
                                )
                            }
                        }

                    val idMillisecondsPairs = alarmDao.insertAlarmMangers(entities)
                    alarmScheduler.schedule(
                        idMillisecondsPairs,
                        onScheduleCompleted = onScheduleCompleted,
                        onScheduleDenied = onScheduleDenied
                    )

                },
                onPermissionDenied = onScheduleDenied
            )
        }
    }

}