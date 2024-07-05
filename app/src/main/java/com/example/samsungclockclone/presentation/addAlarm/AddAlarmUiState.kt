package com.example.samsungclockclone.presentation.addAlarm

data class AddAlarmUiState(
    val daysOfWeek: List<String> = emptyList(),
    val selectedDaysOfWeek: List<String> = emptyList()
)