package com.example.samsungclockclone.presentation.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmDao: AlarmDao
) : ViewModel() {

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
    }

    init {
        viewModelScope.launch {
            // TODO: 1. synchronize to clock tick
            //       2. refresh each minute
            alarmDao.getAlarmAndAlarmManagers().collectLatest { alarms ->
                alarmItems.value = alarms.map { alarmWithAlarmManager ->
                    val firstFireTime =
                        alarmWithAlarmManager.alarmMangerEntityList.minOf { it.fireTime }
                    val mode = alarmWithAlarmManager.alarmEntity.mode
                    val enable = alarmWithAlarmManager.alarmEntity.enable

                    AlarmItem(firstFireTime, mode, enable)
                }
            }
        }
    }

}