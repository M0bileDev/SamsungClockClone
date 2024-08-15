package com.example.samsungclockclone.presentation.editAlarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.model.AlarmOrder
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
import com.example.samsungclockclone.domain.preferences.AlarmPreferences
import com.example.samsungclockclone.domain.scheduler.AlarmId
import com.example.samsungclockclone.presentation.editAlarm.utils.ALARM_ID_KEY
import com.example.samsungclockclone.ui.customViews.dragAndDrop.Index
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.move
import com.example.samsungclockclone.usecase.DeleteAlarmUseCase
import com.example.samsungclockclone.usecase.DeleteAllAlarmsUseCase
import com.example.samsungclockclone.usecase.GetEditAlarmItemsCustomOrderUseCase
import com.example.samsungclockclone.usecase.GetEditAlarmItemsUseCase
import com.example.samsungclockclone.usecase.TurnOffAlarmItemUseCase
import com.example.samsungclockclone.usecase.TurnOnAlarmUseCase
import com.example.samsungclockclone.usecase.UpdateAlarmCustomOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAlarmViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val alarmPreferences: AlarmPreferences,
    private val getEditAlarmItemsUseCase: GetEditAlarmItemsUseCase,
    private val getEditAlarmItemsCustomOrderUseCase: GetEditAlarmItemsCustomOrderUseCase,
    private val turnOnAlarmUseCase: TurnOnAlarmUseCase,
    private val turnOffAlarmItemUseCase: TurnOffAlarmItemUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val deleteAllAlarmsUseCase: DeleteAllAlarmsUseCase,
    private val updateAlarmCustomOrderUseCase: UpdateAlarmCustomOrderUseCase
) : ViewModel() {

    sealed interface EditAlarmAction {
        data object NavigateBack : EditAlarmAction
    }

    private val editAlarmActions = Channel<EditAlarmAction>()
    val actions = editAlarmActions.receiveAsFlow()

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
        val selectedAlarmsCount = editAlarmItems.count { it.selected }

        EditAlarmUiState(
            editAlarmItems,
            allSelected,
            turnOnEnabled,
            turnOffEnabled,
            deleteEnabled,
            deleteAllEnabled,
            selectedAlarmsCount
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
                    getEditAlarmItemsUseCase(
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
                    getEditAlarmItemsCustomOrderUseCase(
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
        }

        editAlarmActions.send(EditAlarmAction.NavigateBack)
    }


    fun onTurnOff() = viewModelScope.launch {
        val selectedEditAlarms = getSelectedEditAlarms()

        selectedEditAlarms.forEach { editAlarm ->
            turnOffAlarmItemUseCase(editAlarm.alarmItem.alarmId, this)
        }

        editAlarmActions.send(EditAlarmAction.NavigateBack)
    }


    fun onDelete() = viewModelScope.launch {
        val selectedEditAlarms = getSelectedEditAlarms()

        selectedEditAlarms.forEach { editAlarm ->
            deleteAlarmUseCase(editAlarm.alarmItem.alarmId, this)
        }

        editAlarmActions.send(EditAlarmAction.NavigateBack)
    }


    private fun getSelectedEditAlarms() = editAlarmItems.value.filter { it.selected }

    fun onDeleteAll() = viewModelScope.launch {
        deleteAllAlarmsUseCase(this)
        editAlarmActions.send(EditAlarmAction.NavigateBack)
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