package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.data.local.model.NotificationAlarm
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetNotificationAlarmUseCase @Inject constructor(
    private val databaseSource: DatabaseSource
) {

    suspend operator fun invoke(
        alarmId: AlarmId,
        alarmManagerId: AlarmManagerId,
        onDataCompleted: (NotificationAlarm) -> Unit,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            val data = databaseSource.getNotificationAlarm(alarmId, alarmManagerId)
            onDataCompleted(data)
        }
    }
}