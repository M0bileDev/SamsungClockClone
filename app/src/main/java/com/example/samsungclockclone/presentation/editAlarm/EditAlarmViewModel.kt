package com.example.samsungclockclone.presentation.editAlarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
import com.example.samsungclockclone.domain.preferences.AlarmOrder
import com.example.samsungclockclone.domain.preferences.AlarmPreferences
import com.example.samsungclockclone.domain.scheduler.AlarmId
import com.example.samsungclockclone.presentation.editAlarm.utils.ALARM_ID_KEY
import com.example.samsungclockclone.ui.customViews.dragAndDrop.Index
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.move
import com.example.samsungclockclone.usecase.DeleteAlarmUseCase
import com.example.samsungclockclone.usecase.DeleteAllAlarmsUseCase
import com.example.samsungclockclone.usecase.GetAllAlarmsAndAlarmManagersUseCase
import com.example.samsungclockclone.usecase.GetAllAlarmsWithAlarmManagersCustomOrderUseCase
import com.example.samsungclockclone.usecase.TurnOffAlarmItemUseCase
import com.example.samsungclockclone.usecase.TurnOnAlarmUseCase
import com.example.samsungclockclone.usecase.UpdateAlarmCustomOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAlarmViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val alarmPreferences: AlarmPreferences,
    private val getAllAlarmsAndAlarmManagersUseCase: GetAllAlarmsAndAlarmManagersUseCase,
    private val getAllAlarmsWithAlarmManagersCustomOrderUseCase: GetAllAlarmsWithAlarmManagersCustomOrderUseCase,
    private val turnOnAlarmUseCase: TurnOnAlarmUseCase,
    private val turnOffAlarmItemUseCase: TurnOffAlarmItemUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val deleteAllAlarmsUseCase: DeleteAllAlarmsUseCase,
    private val updateAlarmCustomOrderUseCase: UpdateAlarmCustomOrderUseCase
) : ViewModel() {

    private val editAlarmItems = MutableStateFlow(emptyList<EditAlarmItem>())
    private val allSelected = MutableStateFlow(false)

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

        viewModelScope.launch {
            val deferredAlarmOrder = async { alarmPreferences.collectAlarmOrder().first() }
            val order = deferredAlarmOrder.await()

            when (order) {
                AlarmOrder.DEFAULT -> {
                    getAllAlarmsAndAlarmManagersUseCase(
                        alarmId,
                        { mappedAlarms ->
                            editAlarmItems.value = mappedAlarms
                            //Handle specific case passing alarm id through argument
                            allSelected.value = mappedAlarms.all { it.selected }
                        },
                        this
                    )
                }

                AlarmOrder.ALARM_TIME_ORDER -> TODO()
                AlarmOrder.CUSTOM_ORDER -> {
                    getAllAlarmsWithAlarmManagersCustomOrderUseCase(
                        alarmId,
                        { mappedAlarms ->
                            editAlarmItems.value = mappedAlarms
                            //Handle specific case passing alarm id through argument
                            allSelected.value = mappedAlarms.all { it.selected }
                        },
                        this
                    )
                }
            }
        }
    }

    private fun handleAlarmId(): AlarmId {
        return checkNotNull(savedStateHandle[ALARM_ID_KEY])
    }

    fun onSelectionChanged(alarmId: AlarmId) {
        val updatedEditAlarmItems = editAlarmItems.value.map {
            it.copy(selected = if (alarmId == it.alarmItem.alarmId) !it.selected else it.selected)
        }
        allSelected.value = updatedEditAlarmItems.all { it.selected }
        editAlarmItems.value = updatedEditAlarmItems
    }

    fun onSelectionAllChanged() {
        val updatedEditAlarmItems = editAlarmItems.value.map {
            it.copy(selected = !allSelected.value)
        }
        allSelected.value = updatedEditAlarmItems.all { it.selected }
        editAlarmItems.value = updatedEditAlarmItems
    }

    fun onTurnOn() = viewModelScope.launch {
        val selectedEditAlarms = getSelectedEditAlarms()

        selectedEditAlarms.forEach { editAlarm ->
            turnOnAlarmUseCase(editAlarm.alarmItem.alarmId, this)
            // TODO: set alarm manager after full db implementation
            // TODO: navigate back to AlarmScreen
        }

    }


    fun onTurnOff() = viewModelScope.launch {
        val selectedEditAlarms = getSelectedEditAlarms()

        selectedEditAlarms.forEach { editAlarm ->
            turnOffAlarmItemUseCase(editAlarm.alarmItem.alarmId, this)
            // TODO: navigate back to AlarmScreen
        }
    }


    fun onDelete() = viewModelScope.launch {
        val selectedEditAlarms = getSelectedEditAlarms()

        selectedEditAlarms.forEach { editAlarm ->
            deleteAlarmUseCase(editAlarm.alarmItem.alarmId, this)
            // TODO: navigate back to AlarmScreen
        }

    }


    private fun getSelectedEditAlarms() = editAlarmItems.value.filter { it.selected }

    fun onDeleteAll() = viewModelScope.launch {
        deleteAllAlarmsUseCase(this)
        // TODO: navigate back to AlarmScreen
    }


    fun onMove(fromIndex: Index, toIndex: Index) {
        val mutableEditAlarmItems = editAlarmItems.value.toMutableList()
        mutableEditAlarmItems.move(fromIndex, toIndex)
        editAlarmItems.value = mutableEditAlarmItems
    }

    fun onMoveCompleted() = viewModelScope.launch {
        val pairAlarmIdCustomOrderList =
            editAlarmItems.value.mapIndexed { index, editAlarmItem -> editAlarmItem.alarmItem.alarmId to index }
        updateAlarmCustomOrderUseCase(pairAlarmIdCustomOrderList, this)
        // TODO: change sort preferences to custom order
    }
}