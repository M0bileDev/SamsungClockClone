package com.example.samsungclockclone.presentation.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.preferences.AlarmOrder
import com.example.samsungclockclone.domain.preferences.AlarmPreferences
import com.example.samsungclockclone.domain.scheduler.AlarmId
import com.example.samsungclockclone.domain.scheduler.AlarmScheduler
import com.example.samsungclockclone.presentation.alarm.utils.EditAlarmMode
import com.example.samsungclockclone.usecase.GetAlarmItemsCustomOrderUseCase
import com.example.samsungclockclone.usecase.GetAlarmItemsUseCase
import com.example.samsungclockclone.usecase.UpdateAlarmEnableSwitchUseCase
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
class AlarmViewModel @Inject constructor(
    private val alarmPreferences: AlarmPreferences,
    private val getAlarmItemsUseCase: GetAlarmItemsUseCase,
    private val getAlarmItemsCustomOrderUseCase: GetAlarmItemsCustomOrderUseCase,
    private val updateAlarmEnableSwitchUseCase: UpdateAlarmEnableSwitchUseCase
) : ViewModel() {

    sealed interface AlarmAction {
        data class EditAlarm(val alarmId: AlarmId = -1L) : AlarmAction
    }

    private val alarmActions = Channel<AlarmAction>()
    val actions = alarmActions.receiveAsFlow()

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
            val deferredAlarmOrder = async { alarmPreferences.collectAlarmOrder().first() }
            val order = deferredAlarmOrder.await()

            when (order) {
                AlarmOrder.DEFAULT -> {
                    getAlarmItemsUseCase(
                        { mapped ->
                            alarmItems.value = mapped
                        },
                        this
                    )
                }

                AlarmOrder.ALARM_TIME_ORDER -> TODO()
                AlarmOrder.CUSTOM_ORDER -> {
                    getAlarmItemsCustomOrderUseCase(
                        { mapped ->
                            alarmItems.value = mapped
                        },
                        this
                    )
                }
            }
        }
    }

    fun onAlarmEnableSwitch(alarmId: AlarmId) {
        viewModelScope.launch {
            updateAlarmEnableSwitchUseCase(alarmId, this)
        }
    }

    fun onEdit(editAlarmMode: EditAlarmMode) {
        viewModelScope.launch {
            when (editAlarmMode) {
                is EditAlarmMode.EditAlarmItemAction -> {
                    alarmActions.send(AlarmAction.EditAlarm(editAlarmMode.alarmId))
                }

                is EditAlarmMode.EditAlarmToolbarAction -> {
                    alarmActions.send(AlarmAction.EditAlarm())
                }
            }
        }
    }

}