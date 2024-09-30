package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.model.AlarmWithAlarmManagerEntity
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAlarmByIdUseCase @Inject constructor(
    private val databaseSource: DatabaseSource
) {

    suspend operator fun invoke(
        alarmId: AlarmId,
        onDataCompleted: (AlarmWithAlarmManagerEntity) -> Unit,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            val data = databaseSource.getAlarmAndAlarmManagersById(alarmId)
            onDataCompleted(data)
        }
    }
}