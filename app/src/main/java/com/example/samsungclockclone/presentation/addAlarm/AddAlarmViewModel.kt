package com.example.samsungclockclone.presentation.addAlarm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddAlarmViewModel : ViewModel() {

    private val addAlarmUiState = MutableStateFlow(AddAlarmUiState())
    val uiState = addAlarmUiState.asStateFlow()

}
