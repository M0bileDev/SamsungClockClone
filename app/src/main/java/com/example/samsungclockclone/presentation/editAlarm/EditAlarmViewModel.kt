package com.example.samsungclockclone.presentation.editAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
import com.example.samsungclockclone.domain.utils.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditAlarmViewModel @Inject constructor(
    alarmDao: AlarmDao
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val editAlarmItems = MutableStateFlow(emptyList<EditAlarmItem>())

    val uiState = combine(editAlarmItems) { editAlarmItems ->

        EditAlarmUiState(
            editAlarmItems[0]
        )
    }


    init {
        viewModelScope.launch {
            alarmDao.collectAlarmAndAlarmManagers().collectLatest { alarms ->
                editAlarmItems.value = alarms.map { alarmWithManagers ->

                    val firstFireTime =
                        alarmWithManagers.alarmMangerEntityList.minOf { it.fireTime }

                    val selectedDaysOfWeek =
                        alarmWithManagers.alarmMangerEntityList.map { alarmManager ->
                            val tmpCalendar = calendar.apply {
                                timeInMillis = alarmManager.fireTime
                            }
                            val calendarDayOfWeek = tmpCalendar.get(Calendar.DAY_OF_WEEK)
                            DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek(
                                calendarDayOfWeek
                            )
                        }

                    with(alarmWithManagers.alarmEntity) {
                        EditAlarmItem(
                            alarmItem = AlarmItem(
                                id,
                                name,
                                firstFireTime,
                                mode,
                                enable,
                                selectedDaysOfWeek
                            )
                        )
                    }
                }
            }
        }
    }


}
