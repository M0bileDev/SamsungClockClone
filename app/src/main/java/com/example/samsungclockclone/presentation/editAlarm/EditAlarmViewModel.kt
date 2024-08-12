package com.example.samsungclockclone.presentation.editAlarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
import com.example.samsungclockclone.domain.preferences.AlarmOrder
import com.example.samsungclockclone.domain.preferences.AlarmPreferences
import com.example.samsungclockclone.domain.scheduler.AlarmId
import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.domain.utils.DayOfWeek
import com.example.samsungclockclone.presentation.editAlarm.utils.ALARM_ID_KEY
import com.example.samsungclockclone.ui.customViews.dragAndDrop.Index
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.move
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
    private val savedStateHandle: SavedStateHandle,
    private val alarmPreferences: AlarmPreferences,
    private val alarmDao: AlarmDao
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val editAlarmItems = MutableStateFlow(emptyList<EditAlarmItem>())
    private val allSelected = MutableStateFlow(false)
    private val alarmOrder = MutableStateFlow(AlarmOrder.DEFAULT)

    val uiState = combine(editAlarmItems, allSelected) { editAlarmItems, allSelected ->

        val turnOnEnabled =
            editAlarmItems.any { it.selected && !it.alarmItem.enable }
        val turnOffEnabled =
            editAlarmItems.any { it.selected && it.alarmItem.enable }
        val deleteEnabled =
            editAlarmItems.any { it.selected } && !editAlarmItems.all { it.selected }
        val deleteAllEnabled = editAlarmItems.all { it.selected }

        EditAlarmUiState(
            editAlarmItems,
            allSelected,
            turnOnEnabled,
            turnOffEnabled,
            deleteEnabled,
            deleteAllEnabled
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        EditAlarmUiState()
    )

    init {
        val alarmId = handleAlarmId()
        // TODO: Fix this mess
        //  1) Mapper
        //  2) Extract repeated logic -> Strategy patter?

        viewModelScope.launch {
            launch {
                alarmPreferences.collectAlarmOrder().collectLatest { order ->
                    alarmOrder.value = order
                }
            }

            launch {
                when (alarmOrder.value) {
                    AlarmOrder.DEFAULT -> {
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

                            editAlarmItems.value = mappedAlarms
                            //Handle specific case passing alarm id through argument
                            allSelected.value = mappedAlarms.all { it.selected }
                        }
                    }

                    AlarmOrder.ALARM_TIME_ORDER -> TODO()
                    AlarmOrder.CUSTOM_ORDER -> {
                        alarmDao.collectAllAlarmAndAlarmManagersCustomOrder()
                            .collectLatest { alarms ->
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

                                editAlarmItems.value = mappedAlarms
                                //Handle specific case passing alarm id through argument
                                allSelected.value = mappedAlarms.all { it.selected }
                            }
                    }
                }
            }
        }
    }

    private fun handleAlarmId(): AlarmId {
        return checkNotNull(savedStateHandle[ALARM_ID_KEY])
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

    fun onTurnOn() {
        viewModelScope.launch {
            val selectedEditAlarms = editAlarmItems.value.filter { it.selected }

            selectedEditAlarms.forEach { editAlarm ->
                val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(editAlarm.alarmItem.alarmId)
                val updatedAlarm = alarm.copy(enable = true)
                alarmDao.updateAlarm(updatedAlarm)

                // TODO: set alarm manager after full db implementation
                // TODO: navigate back to AlarmScreen
            }

        }

    }

    fun onTurnOff() {
        viewModelScope.launch {
            val selectedEditAlarms = editAlarmItems.value.filter { it.selected }

            selectedEditAlarms.forEach { editAlarm ->
                val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(editAlarm.alarmItem.alarmId)
                val updatedAlarm = alarm.copy(enable = false)
                alarmDao.updateAlarm(updatedAlarm)

                // TODO: cancel alarm manager after full db implementation
                // TODO: navigate back to AlarmScreen
            }

        }

    }

    fun onDelete() {
        viewModelScope.launch {
            val selectedEditAlarms = editAlarmItems.value.filter { it.selected }

            selectedEditAlarms.forEach { editAlarm ->
                val (alarm, alarmManagers) = alarmDao.getAlarmAndAlarmManagersById(editAlarm.alarmItem.alarmId)

                alarmManagers.forEach { alarmManager ->
                    // TODO: cancel alarm manager after full db implementation
                }

                alarmDao.deleteAlarm(alarm)
                // TODO: navigate back to AlarmScreen
            }

        }
    }

    fun onDeleteAll() {
        viewModelScope.launch {
            val allAlarmAndAlarmManagers = alarmDao.getAllAlarmAndAlarmManagers()

            val allAlarmManagers = allAlarmAndAlarmManagers.map { it.alarmMangerEntityList }
            allAlarmManagers.forEach { alarmManager ->
                // TODO: cancel alarm manager after full db implementation
            }

            val allAlarms = allAlarmAndAlarmManagers.map { it.alarmEntity }
            alarmDao.deleteAllAlarms(allAlarms)
            // TODO: navigate back to AlarmScreen
        }
    }

    fun onMove(fromIndex: Index, toIndex: Index) {
        val mutableEditAlarmItems = editAlarmItems.value.toMutableList()
        mutableEditAlarmItems.move(fromIndex, toIndex)
        viewModelScope.launch {
            val pairAlarmIdCustomOrderList =
                mutableEditAlarmItems.mapIndexed { index, editAlarmItem -> editAlarmItem.alarmItem.alarmId to index }
            alarmDao.updateAlarmCustomOrderList(pairAlarmIdCustomOrderList)
        }
    }

}