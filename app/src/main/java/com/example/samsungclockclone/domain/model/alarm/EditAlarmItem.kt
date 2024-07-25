package com.example.samsungclockclone.domain.model.alarm

import com.example.samsungclockclone.domain.utils.AlarmMode

data class EditAlarmItem(
    val selected: Boolean = false,
    val dragged: Boolean = false,
    val alarmItem: AlarmItem = AlarmItem()
) {
    companion object {
        val editAlarmItemPreview = EditAlarmItem(
            selected = true,
            dragged = true,
            alarmItem = AlarmItem(
                0L,
                "Preview",
                1721730918345,
                AlarmMode.OnlyTime,
                enable = true
            )
        )
        val editAlarmItemPreview2 = EditAlarmItem(
            selected = false,
            dragged = false,
            alarmItem = AlarmItem(
                0L,
                "Preview",
                1721730918345,
                AlarmMode.OnlyTime,
                enable = true
            )
        )
    }
}