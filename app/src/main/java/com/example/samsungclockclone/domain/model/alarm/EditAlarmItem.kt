package com.example.samsungclockclone.domain.model.alarm

import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.domain.utils.DayOfWeek

data class EditAlarmItem(
    val selected: Boolean = false,
    val alarmItem: AlarmItem = AlarmItem()
) {
    companion object {
        val editAlarmItemPreview = EditAlarmItem(
            selected = true,
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
            alarmItem = AlarmItem(
                0L,
                "Preview",
                1721730918345,
                AlarmMode.OnlyTime,
                enable = true
            )
        )
        val editAlarmItemPreview3 = EditAlarmItem(
            selected = false,
            alarmItem = AlarmItem(
                0L,
                "Preview",
                1721730918345,
                AlarmMode.DayOfWeekAndTime,
                enable = true,
                selectedDaysOfWeek = listOf(DayOfWeek.Friday, DayOfWeek.Monday)
            )
        )
    }
}