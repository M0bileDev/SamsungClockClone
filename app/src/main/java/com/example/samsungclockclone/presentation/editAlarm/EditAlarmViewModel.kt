package com.example.samsungclockclone.presentation.editAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.scheduler.AlarmId
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
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
class EditAlarmViewModel @Inject constructor(
    alarmDao: AlarmDao
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val editAlarmItems = MutableStateFlow(emptyList<EditAlarmItem>())
    private val allSelected = MutableStateFlow(false)

    val uiState = combine(editAlarmItems, allSelected) { editAlarmItems, allSelected ->

        EditAlarmUiState(
            editAlarmItems,
            allSelected
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        EditAlarmUiState()
    )

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

    fun onSelectionChanged(alarmId: AlarmId) {
        val mutableEditAlarmItems = editAlarmItems.value
        val updatedEditAlarmItems = mutableEditAlarmItems.map {
            it.copy(selected = if (alarmId == it.alarmItem.alarmId) !it.selected else it.selected)
        }
        allSelected.value = updatedEditAlarmItems.all { it.selected }
        editAlarmItems.value = updatedEditAlarmItems
    }

    fun onSelectionAllChanged() {
        val mutableEditAlarmItems = editAlarmItems.value
        val updatedEditAlarmItems = mutableEditAlarmItems.map {
            it.copy(selected = !allSelected.value)
        }
        allSelected.value = updatedEditAlarmItems.all { it.selected }
        editAlarmItems.value = updatedEditAlarmItems
    }

}
