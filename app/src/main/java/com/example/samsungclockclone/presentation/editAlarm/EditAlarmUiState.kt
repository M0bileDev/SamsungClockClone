package com.example.samsungclockclone.presentation.editAlarm

import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem

data class EditAlarmUiState(
    val editAlarmItems: List<EditAlarmItem> = emptyList(),
    // TODO: Create enum type of bottom buttons
) {
    companion object {
        val editAlarmUiStatePreview = EditAlarmUiState(
            listOf(
                EditAlarmItem(
                    selected = false,
                    dragged = false,
                    AlarmItem.alarmItemPreview
                ),
                EditAlarmItem(
                    selected = true,
                    dragged = false,
                    AlarmItem.alarmItemPreview2
                ),
                EditAlarmItem(
                    selected = true,
                    dragged = true,
                    AlarmItem.alarmItemPreview3
                ),
            )
        )
    }
}