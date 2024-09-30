package com.example.samsungclockclone.presentation.screens.editAlarm

import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem

data class EditAlarmUiState(
    val editAlarmItems: List<EditAlarmItem> = emptyList(),
    val allSelected: Boolean = false,
    val turnOnEnabled: Boolean = false,
    val turnOffEnabled: Boolean = false,
    val deleteEnabled: Boolean = false,
    val deleteAllEnabled: Boolean = false,
    val selectedAlarmsCount: Int = 0
) {
    companion object {
        val editAlarmUiStatePreview = EditAlarmUiState(
            listOf(
                EditAlarmItem(
                    selected = false,
                    AlarmItem.alarmItemPreview
                ),
                EditAlarmItem(
                    selected = true,
                    AlarmItem.alarmItemPreview2
                ),
                EditAlarmItem(
                    selected = true,
                    AlarmItem.alarmItemPreview3
                ),
            )
        )

        val editAlarmUiStatePreview2 = EditAlarmUiState(
            turnOnEnabled = true,
            turnOffEnabled = true,
            deleteEnabled = true,
            editAlarmItems = listOf(
                EditAlarmItem(
                    selected = false,
                    AlarmItem.alarmItemPreview
                ),
                EditAlarmItem(
                    selected = true,
                    AlarmItem.alarmItemPreview2
                ),
                EditAlarmItem(
                    selected = true,
                    AlarmItem.alarmItemPreview3
                ),
            )
        )

        val editAlarmUiStatePreview3 = EditAlarmUiState(
            turnOnEnabled = true,
            turnOffEnabled = true,
            deleteAllEnabled = true,
            allSelected = true,
            editAlarmItems = listOf(
                EditAlarmItem(
                    selected = true,
                    AlarmItem.alarmItemPreview
                ),
                EditAlarmItem(
                    selected = true,
                    AlarmItem.alarmItemPreview2
                ),
                EditAlarmItem(
                    selected = true,
                    AlarmItem.alarmItemPreview3
                ),
            )
        )
    }
}