package com.example.samsungclockclone.presentation.screens.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.model.AlarmOrder
import com.example.samsungclockclone.domain.model.alarm.AlarmDifference
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.AlarmTitleString
import com.example.samsungclockclone.domain.model.alarm.DifferenceType
import com.example.samsungclockclone.usecase.preferences.AlarmPreferences
import com.example.samsungclockclone.usecase.ticker.TimeTicker
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.presentation.screens.alarm.utils.AddAlarmMode
import com.example.samsungclockclone.presentation.screens.alarm.utils.EditAlarmMode
import com.example.samsungclockclone.usecase.GetAlarmItemsCustomOrderUseCase
import com.example.samsungclockclone.usecase.GetAlarmItemsUseCase
import com.example.samsungclockclone.usecase.UpdateAlarmEnableSwitchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
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
    private val updateAlarmEnableSwitchUseCase: UpdateAlarmEnableSwitchUseCase,
    private val timeTicker: TimeTicker
) : ViewModel() {

    private var getAlarmItemsJob: Job? = null

    sealed interface AlarmAction {
        data class EditAlarm(val alarmId: AlarmId = -1L) : AlarmAction

        data class AddAlarm(val alarmId: AlarmId = -1L) : AlarmAction

        data object RequestSchedulePermission : AlarmAction
    }

    private val alarmActions = Channel<AlarmAction>()
    val actions = alarmActions.receiveAsFlow()

    private val alarmItems = MutableStateFlow(emptyList<AlarmItem>())
    private val editModeEnable = MutableStateFlow(false)
    private val timeMillis = MutableStateFlow(0L)
    private val displayPermissionRequire = MutableStateFlow(false)

    val uiState = combine(
        alarmItems, editModeEnable, timeMillis, displayPermissionRequire
    ) { alarmItems, editModeEnable, _, displayPermissionRequire ->

        val editAvailable = alarmItems.isNotEmpty()
        val sortAvailable = alarmItems.size > 1
        val alarmsOff = alarmItems.all { !it.enable }
        val alarmTitleString = if (alarmsOff) {
            AlarmTitleString.AlarmsOff
        } else {
            val enabledAlarms = alarmItems.filter { it.enable }
            createAlarmTitleStringNearestAlarm(enabledAlarms)
        }

        AlarmUiState(
            alarmItems,
            editAvailable,
            sortAvailable,
            editModeEnable,
            alarmTitleString,
            displayPermissionRequire
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AlarmUiState()
    )

    init {
        getAlarmItems()
        synchronizeWithTimeChanged()
    }

    private fun synchronizeWithTimeChanged() {
        viewModelScope.launch {
            timeTicker.onGetTick().collectLatest {
                timeMillis.value = it
            }
        }
    }

    private fun createAlarmTitleStringNearestAlarm(alarmItems: List<AlarmItem>): AlarmTitleString.NearestAlarm {
        val nearestFireTime = alarmItems.map { it.fireTime }.minOf { it }
        val difference = nearestFireTime - System.currentTimeMillis()
        val days = difference / (1000 * 60 * 60 * 24)
        val hours = difference / (1000 * 60 * 60) % 24
        val minutes = difference / (1000 * 60) % 60
        val differenceType = when {
            days > 0 -> DifferenceType.DAYS
            hours > 0 -> DifferenceType.HOURS_MINUTES
            else -> DifferenceType.MINUTES
        }
        return AlarmTitleString.NearestAlarm(
            nearestFireTime,
            AlarmDifference(days.toInt(), hours.toInt(), minutes.toInt(), differenceType)
        )
    }

    private fun getAlarmItems() {
        getAlarmItemsJob?.cancel()
        getAlarmItemsJob = viewModelScope.launch {
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

                AlarmOrder.ALARM_TIME_ORDER -> {
                    //Not implemented yet
                }

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

    fun onAlarmEnableSwitch(alarmId: AlarmId) = viewModelScope.launch {
        updateAlarmEnableSwitchUseCase(
            alarmId,
            parentScope = this,
            onScheduleDenied = ::onScheduleDenied
        )
    }

    private fun onScheduleDenied() {
        displayPermissionRequire.value = true
    }

    fun onRequestSchedulePermission() {
        displayPermissionRequire.value = false
        viewModelScope.launch {
            alarmActions.send(AlarmAction.RequestSchedulePermission)
        }
    }

    fun dismissSchedulePermission() {
        displayPermissionRequire.value = false
    }


    fun onEdit(editAlarmMode: EditAlarmMode) = viewModelScope.launch {
        when (editAlarmMode) {
            is EditAlarmMode.EditAlarmItemAction -> {
                alarmActions.send(AlarmAction.EditAlarm(editAlarmMode.alarmId))
            }

            is EditAlarmMode.EditAlarmToolbarAction -> {
                alarmActions.send(AlarmAction.EditAlarm())
            }
        }
    }

    fun onSort(alarmOrder: AlarmOrder) = viewModelScope.launch {
        alarmPreferences.saveAlarmOrder(alarmOrder)
        getAlarmItems()
    }

    fun onAdd(addAlarmMode: AddAlarmMode) = viewModelScope.launch {
        when (addAlarmMode) {
            is AddAlarmMode.AddAlarmItemAction -> {
                alarmActions.send(AlarmAction.AddAlarm(addAlarmMode.alarmId))
            }

            is AddAlarmMode.AddAlarmToolbarAction -> {
                alarmActions.send(AlarmAction.AddAlarm())
            }
        }
    }

}