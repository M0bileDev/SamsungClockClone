package com.example.samsungclockclone.usecase

import android.app.AlarmManager
import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.domain.model.AlarmRepeat.Companion.createRepeatMillis
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.framework.ext.suspendCheckPermission
import com.example.samsungclockclone.usecase.scheduler.AlarmScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class RescheduleAlarmManagerUseCase @Inject constructor(
    private val databaseSource: DatabaseSource,
    private val alarmScheduler: AlarmScheduler,
    private val alarmManager: AlarmManager
) {

    suspend operator fun invoke(
        alarmId: AlarmId,
        alarmManagerId: AlarmManagerId,
        onPermissionDenied: () -> Unit ={},
        onScheduleCompleted: () -> Unit = {},
        onScheduleDenied: () -> Unit = {},
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!parentScope.isActive) return@launch

            alarmManager.suspendCheckPermission(
                this,
                onPermissionGranted = {
                    val alarmManager = databaseSource.getAlarmManagerById(alarmManagerId)
                    val fireTime = alarmManager.fireTime
                    val repeat = alarmManager.repeat

                    val repeatMillis = repeat.createRepeatMillis()
                    val updatedMillis = fireTime + repeatMillis

                    databaseSource.updateAlarmManagerOutOfDate(alarmManager.uniqueId, updatedMillis)

                    alarmScheduler.schedule(
                        alarmId,
                        listOf(alarmManager.uniqueId to updatedMillis),
                        onScheduleCompleted = onScheduleCompleted,
                        onScheduleDenied = onScheduleDenied
                    )
                },
                onPermissionDenied = onPermissionDenied
            )
        }
    }
}