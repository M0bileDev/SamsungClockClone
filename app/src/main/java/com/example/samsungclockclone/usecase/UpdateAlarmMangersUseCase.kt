package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.framework.ticker.TimeTicker
import com.example.samsungclockclone.domain.model.AlarmRepeat.Companion.createRepeatMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateAlarmMangersUseCase @Inject constructor(
    private val databaseSource: DatabaseSource,
    private val ticker: TimeTicker
) {

    private var scopedJob: Job? = null

    suspend operator fun invoke(
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ) {

        //Launch update without waiting on time tick (~1min), when time tick pass, switch
        // job execution on collector
        scopedJob?.cancel()
        scopedJob = parentScope.launch(dispatcher) {
            updateDisabledOutDateAlarmManagers(this, onReturn = {
                return@updateDisabledOutDateAlarmManagers
            })
        }

        ticker.onGetTick().collectLatest {
            scopedJob?.cancel()
            scopedJob = parentScope.launch(dispatcher) {
                updateDisabledOutDateAlarmManagers(this, onReturn = {
                    return@updateDisabledOutDateAlarmManagers
                })
            }
        }
    }

    private suspend fun updateDisabledOutDateAlarmManagers(
        coroutineScope: CoroutineScope,
        onReturn: () -> Unit
    ) = with(coroutineScope) {
        if (!isActive) return

        val actualMillis = System.currentTimeMillis()
        val parentIds = databaseSource.getDisabledAlarmIds()
        val managers = databaseSource.getAlarmManagersOutOfDateByIds(parentIds, actualMillis)

        val pairsIdMillis = managers.map { alarmManager ->
            if (!isActive) onReturn()

            val fireTime = alarmManager.fireTime
            val repeat = alarmManager.repeat

            val repeatMillis = repeat.createRepeatMillis()
            var updatedMillis = fireTime + repeatMillis

            while (isActive && updatedMillis < actualMillis) {
                updatedMillis += repeatMillis
            }
            alarmManager.uniqueId to updatedMillis
        }

        databaseSource.updateAlarmManagersOutOfDate(pairsIdMillis)
    }
}