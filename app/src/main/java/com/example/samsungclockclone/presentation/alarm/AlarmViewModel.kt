package com.example.samsungclockclone.presentation.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.scheduler.AlarmId
import com.example.samsungclockclone.data.local.scheduler.AlarmScheduler
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.utils.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val alarmDao: AlarmDao
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val alarmItems = MutableStateFlow(emptyList<AlarmItem>())
    private val editModeEnable = MutableStateFlow(false)

    val uiState = combine(
        alarmItems, editModeEnable
    ) { alarmItems, editModeEnable ->

        val editAvailable = alarmItems.isNotEmpty()
        val sortAvailable = alarmItems.size > 2

        AlarmUiState(
            alarmItems,
            editAvailable,
            sortAvailable,
            editModeEnable
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AlarmUiState()
    )

    init {
        viewModelScope.launch {
            // TODO: 1. synchronize to clock tick
            //       2. refresh each minute
            //       3. Extract logic to separate function
            alarmDao.collectAlarmAndAlarmManagers().collectLatest { alarms ->
                alarmItems.value = alarms.map { alarmWithAlarmManager ->
                    val firstFireTime =
                        alarmWithAlarmManager.alarmMangerEntityList.minOf { it.fireTime }

                    val selectedDaysOfWeek =
                        alarmWithAlarmManager.alarmMangerEntityList.map { alarmManager ->
                            val tmpCalendar = calendar.apply {
                                timeInMillis = alarmManager.fireTime
                            }
                            val calendarDayOfWeek = tmpCalendar.get(Calendar.DAY_OF_WEEK)
                            DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek(
                                calendarDayOfWeek
                            )
                        }

                    with(alarmWithAlarmManager.alarmEntity) {
                        AlarmItem(
                            id,
                            name,
                            firstFireTime,
                            mode,
                            enable,
                            selectedDaysOfWeek = selectedDaysOfWeek
                        )
                    }
                }
            }
        }
    }

    fun updateAlarm(alarmId: AlarmId) {
        viewModelScope.launch {
            val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(alarmId)
            val updatedAlarm = alarm.copy(enable = !alarm.enable)
            alarmDao.updateAlarm(updatedAlarm)

            // TODO: enable after database full implementation
//            alarmManagers.forEach { alarmManager ->
//                alarmScheduler.cancel(alarmManager.uniqueId)
//            }
        }
    }

}