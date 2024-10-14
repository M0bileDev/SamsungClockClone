package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateAlarmOngoingUseCase @Inject constructor(
    private val databaseSource: DatabaseSource,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase
) {

    suspend operator fun invoke(
        alarmId: AlarmId,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!parentScope.isActive) return@launch

            val updateAlarmOngoing: (Long, Boolean) -> Unit = { id, ongoing ->
                this.launch {
                    databaseSource.updateAlarmOngoingById(id, !ongoing)
                }
            }

            getAlarmByIdUseCase(
                alarmId,
                onDataCompleted = { data ->
                    val alarm = data.alarmEntity
                    with(alarm) {
                        updateAlarmOngoing(id, ongoing)
                    }
                },
                parentScope = this
            )
        }
    }
}