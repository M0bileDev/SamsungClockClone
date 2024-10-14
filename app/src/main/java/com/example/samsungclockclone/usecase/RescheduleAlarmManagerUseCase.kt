package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.domain.model.AlarmRepeat.Companion.createRepeatMillis
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class RescheduleAlarmManagerUseCase @Inject constructor(
    private val databaseSource: DatabaseSource
) {

    suspend operator fun invoke(
        alarmManagerId: AlarmManagerId,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!parentScope.isActive) return@launch

            val alarmManager = databaseSource.getAlarmManagerById(alarmManagerId)
            val fireTime = alarmManager.fireTime
            val repeat = alarmManager.repeat

            val repeatMillis = repeat.createRepeatMillis()
            val updatedMillis = fireTime + repeatMillis

            databaseSource.updateAlarmManagerOutOfDate(alarmManager.uniqueId, updatedMillis)
        }
    }
}