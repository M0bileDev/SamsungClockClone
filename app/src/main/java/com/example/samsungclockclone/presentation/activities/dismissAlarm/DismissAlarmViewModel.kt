package com.example.samsungclockclone.presentation.activities.dismissAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.usecase.GetNotificationAlarmUseCase
import com.example.samsungclockclone.usecase.notification.NotificationBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DismissAlarmViewModel @Inject constructor(
    private val notificationBuilder: NotificationBuilder,
    private val getNotificationAlarmUseCase: GetNotificationAlarmUseCase,
) : ViewModel() {

    // TODO: Implement actions collection inside Activity
    private val _dismissAlarmActions = Channel<DismissAlarmState>()
    private val dismissAlarmActions = _dismissAlarmActions.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            _dismissAlarmActions.send(DismissAlarmState.Idle)
        }
    }

    sealed interface DismissAlarmState {
        data object Idle : DismissAlarmState
        data class Ongoing(val dismissAlarmUiState: DismissAlarmUiState) : DismissAlarmState
    }

    fun loadAlarmData(alarmId: AlarmId, alarmManagerId: AlarmManagerId) = viewModelScope.launch {
        getNotificationAlarmUseCase(
            alarmId,
            alarmManagerId,
            onDataCompleted = { notificationAlarm ->
                viewModelScope.launch(Dispatchers.Default) {
                    with(notificationAlarm) {
                        _dismissAlarmActions.send(
                            DismissAlarmState.Ongoing(
                                DismissAlarmUiState(
                                    fireTime,
                                    name
                                )
                            )
                        )
                    }
                }
            },
            this
        )

    }

    fun onDismiss(alarmManagerId: AlarmManagerId) {
        notificationBuilder.cancelAlarmNotification(alarmManagerId)
    }

}