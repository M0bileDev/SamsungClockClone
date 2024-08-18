package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.domain.utils.AlarmId
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

class UpdateAlarmUseCase @Inject constructor(
    private val alarmDao: AlarmDao
) {

    suspend operator fun invoke(
        alarmId: AlarmId,
        alarmMode: AlarmMode,
        alarmMillisecondsList: List<Long>,
        selectedDaysOfWeek: List<DayOfWeek>,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            alarmDao.updateAlarmMode(alarmId, alarmMode)

            alarmDao.deleteAlarmManagerById(alarmId)
            // TODO: cancel alarm manager after full db implementation

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

            alarmDao.insertAlarmMangers(entities)

            //TODO schedule alarms via alarm manager
//            alarmScheduler.schedule(
//                alarms,
//                onScheduleCompleted = ::onScheduleCompleted,
//                onScheduleDenied = ::onScheduleDenied
//            )

        }
    }

}