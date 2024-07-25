package com.example.samsungclockclone.presentation.editAlarm

import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem

data class EditAlarmUiState(
    val allSelected: Boolean = false,
    val selectedCount: Int = 0,
    val editAlarmItems: List<EditAlarmItem> = emptyList(),
    // TODO: Create enum type of bottom buttons
    val bottomButtons
)