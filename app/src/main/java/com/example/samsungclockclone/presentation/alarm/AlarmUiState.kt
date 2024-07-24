package com.example.samsungclockclone.presentation.alarm

import com.example.samsungclockclone.domain.model.alarm.AlarmItem

data class AlarmUiState(
    val alarmItems: List<AlarmItem> = emptyList(),
    val editAvailable: Boolean = false,
    val sortAvailable: Boolean = false,
    val editModeEnable: Boolean = false
) {
    companion object {
        val alarmUiStatePreview = AlarmUiState(
            listOf(
                AlarmItem.alarmItemPreview,
                AlarmItem.alarmItemPreview2,
                AlarmItem.alarmItemPreview3,
                AlarmItem.alarmItemPreview4
            ),
            editAvailable = true,
            sortAvailable = true,
            editModeEnable = false
        )
    }
}