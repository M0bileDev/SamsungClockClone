package com.example.samsungclockclone.usecase

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.model.DayOfWeek
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class GetEditAlarmItemsUseCase @Inject constructor(
    private val databaseSource: DatabaseSource
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

            databaseSource.collectAllAlarmAndAlarmManagers().collectLatest { alarms ->

                val mappedAlarms = alarms.map { alarmWithAlarmManagerEntity ->

                    val firstFireTime =
                        if (alarmWithAlarmManagerEntity.alarmMangerEntityList.isNotEmpty()) {
                            alarmWithAlarmManagerEntity.alarmMangerEntityList.minOf { it.fireTime }
                        } else 0

                    val selectedDaysOfWeek =
                        if (alarmWithAlarmManagerEntity.alarmEntity.mode == AlarmMode.DayOfWeekAndTime) {
                            alarmWithAlarmManagerEntity.alarmMangerEntityList.map { alarmManager ->
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


                    with(alarmWithAlarmManagerEntity.alarmEntity) {
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

                val filteredAlarms = mappedAlarms.filter { it.alarmItem.fireTime != 0L }
                onMappedAlarms(filteredAlarms)
            }
        }
    }

}