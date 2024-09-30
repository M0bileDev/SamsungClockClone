package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.presentation.customs.dragAndDrop.Index
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateAlarmCustomOrderUseCase @Inject constructor(
    private val alarmDao: AlarmDao
) {

    suspend operator fun invoke(
        pairAlarmIdCustomOrderList: List<Pair<AlarmId, Index>>,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            alarmDao.updateAlarmCustomOrderList(pairAlarmIdCustomOrderList)
        }
    }
}