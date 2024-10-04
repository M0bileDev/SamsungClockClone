package com.example.samsungclockclone.usecase

import android.app.AlarmManager
import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.domain.ext.toAlarmRepeat
import com.example.samsungclockclone.usecase.scheduler.AlarmScheduler
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

class SaveAlarmUseCase @Inject constructor(
    private val databaseSource: DatabaseSource,
    private val alarmScheduler: AlarmScheduler,
    private val alarmManager: AlarmManager
) {

    suspend operator fun invoke(
        alarmMode: AlarmMode,
        alarmName: String,
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
                coroutineScope = this,
                onPermissionGranted = {
                    val alarmEntity = AlarmEntity(
                        mode = alarmMode,
                        name = alarmName,
                        enable = true
                    )
                    val alarmId = databaseSource.insertAlarmUpdateOrder(alarmEntity)

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

                    val idMillisecondsPairs = databaseSource.insertAlarmMangers(entities)

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