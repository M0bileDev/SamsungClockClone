package com.example.samsungclockclone.presentation.activities.dismissAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.usecase.GetNotificationAlarmUseCase
import com.example.samsungclockclone.usecase.notification.NotificationBuilder
import com.example.samsungclockclone.usecase.ringtone.RingtoneController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DismissAlarmViewModel @Inject constructor(
    private val notificationBuilder: NotificationBuilder,
    private val getNotificationAlarmUseCase: GetNotificationAlarmUseCase,
    private val ringtoneController: RingtoneController
) : ViewModel() {

    private val _dismissAlarmState = Channel<DismissAlarmState>()
    val dismissAlarmState = _dismissAlarmState.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, DismissAlarmState.Idle)

    private val _dismissAlarmAction = Channel<DismissAlarmAction>()
    val dismissAlarmAction = _dismissAlarmAction.receiveAsFlow()

    sealed interface DismissAlarmState {
        data object Idle : DismissAlarmState
        data class Ongoing(val dismissAlarmUiState: DismissAlarmUiState) : DismissAlarmState
    }

    sealed interface DismissAlarmAction {
        data object Finish : DismissAlarmAction
    }

    fun loadAlarmData(alarmId: AlarmId, alarmManagerId: AlarmManagerId) =
        viewModelScope.launch(Dispatchers.Default) {
            getNotificationAlarmUseCase(
                alarmId,
                alarmManagerId,
                onDataCompleted = { notificationAlarm ->
                    this.launch onDataCompleted@{
                        if (!isActive) return@onDataCompleted

                        with(notificationAlarm) {
                            _dismissAlarmState.send(
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

    fun onDismiss(id: AlarmManagerId) =
        viewModelScope.launch(Dispatchers.Default) {
            ringtoneController.stop()
            notificationBuilder.cancelAlarmNotification(id)
            _dismissAlarmAction.send(DismissAlarmAction.Finish)
        }

}