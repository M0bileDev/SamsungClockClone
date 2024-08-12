package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
import com.example.samsungclockclone.domain.scheduler.AlarmId
import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.domain.utils.DayOfWeek
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class GetAllAlarmsAndAlarmManagersUseCase @Inject constructor(
    private val alarmDao: AlarmDao
) {

    private val calendar = Calendar.getInstance()

    suspend operator fun invoke(
        alarmId: AlarmId,
        onMappedAlarms: (List<EditAlarmItem>) -> Unit,
        parentScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Job {
        return parentScope.launch(dispatcher) {
            if (!isActive) return@launch

            alarmDao.collectAllAlarmAndAlarmManagers().collectLatest { alarms ->
                val mappedAlarms = alarms.map { alarmWithManagers ->

                    val firstFireTime =
                        alarmWithManagers.alarmMangerEntityList.minOf { it.fireTime }

                    val selectedDaysOfWeek =
                        if (alarmWithManagers.alarmEntity.mode == AlarmMode.DayOfWeekAndTime) {
                            alarmWithManagers.alarmMangerEntityList.map { alarmManager ->
                                val tmpCalendar = calendar.apply {
                                    timeInMillis = alarmManager.fireTime
                                }
                                val calendarDayOfWeek =
                                    tmpCalendar.get(Calendar.DAY_OF_WEEK)
                                DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek(
                                    calendarDayOfWeek
                                )
                            }
                        } else emptyList()


                    with(alarmWithManagers.alarmEntity) {
                        EditAlarmItem(
                            selected = id == alarmId,
                            alarmItem = AlarmItem(
                                id,
                                customOrder,
                                name,
                                firstFireTime,
                                mode,
                                enable,
                                selectedDaysOfWeek = selectedDaysOfWeek
                            )
                        )
                    }
                }

                onMappedAlarms(mappedAlarms)
            }
        }
    }

}