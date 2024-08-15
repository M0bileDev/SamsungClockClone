package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.domain.scheduler.AlarmId
import com.example.samsungclockclone.domain.scheduler.AlarmMilliseconds
import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.domain.utils.DayOfWeek
import com.example.samsungclockclone.domain.utils.toAlarmRepeat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveAlarmUseCase @Inject constructor(
    private val alarmDao: AlarmDao
) {

    suspend operator fun invoke(
        alarmMode: AlarmMode,
        alarmName: String,
        alarmMillisecondsList: List<Long>,
        selectedDaysOfWeek: List<DayOfWeek>,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            val alarmEntity = AlarmEntity(
                mode = alarmMode,
                name = alarmName,
                enable = true
            )
            val alarmId = alarmDao.insertAlarmUpdateOrder(alarmEntity)

            val alarmRepeat = alarmMode.toAlarmRepeat()
            val alarms: List<Pair<AlarmId, AlarmMilliseconds>> =
                if (selectedDaysOfWeek.isNotEmpty()) {
                    val zipped = alarmMillisecondsList.zip(selectedDaysOfWeek)
                    zipped.map {
                        val (milliseconds, day) = it
                        val alarmManagerEntity = AlarmManagerEntity(
                            parentId = alarmId,
                            fireTime = milliseconds,
                            repeat = alarmRepeat,
                            dayOfWeek = day
                        )
                        alarmDao.insertAlarmManager(alarmManagerEntity) to milliseconds
                    }
                } else {
                    alarmMillisecondsList.map { milliseconds ->
                        val alarmManagerEntity = AlarmManagerEntity(
                            parentId = alarmId,
                            fireTime = milliseconds,
                            repeat = alarmRepeat
                        )
                        alarmDao.insertAlarmManager(alarmManagerEntity) to milliseconds
                    }
                }


            // TODO: enable after database full implementation
            // TODO: extract to other use case?
            //schedule alarms via alarm manager
//            alarmScheduler.schedule(
//                alarms,
//                onScheduleCompleted = ::onScheduleCompleted,
//                onScheduleDenied = ::onScheduleDenied
//            )

        }
    }
}