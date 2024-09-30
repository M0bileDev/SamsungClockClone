package com.example.samsungclockclone.domain.model.alarm

import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.model.DayOfWeek

data class AlarmItem(
    val alarmId: AlarmId = 0L,
    val customOrder: Long = 0L,
    val name: String = "",
    val fireTime: Long = 0L,
    val mode: AlarmMode = AlarmMode.OnlyTime,
    val enable: Boolean = false,
    val daysOfWeek: List<DayOfWeek> = DayOfWeek.DayOfWeekHelper.standardWeek(),
    val selectedDaysOfWeek: List<DayOfWeek> = emptyList(),
) {
    companion object {
        val alarmItemPreview = AlarmItem(
            0L,
            0L,
            "Preview",
            1721730918345,
            AlarmMode.OnlyTime,
            enable = true
        )
        val alarmItemPreview2 = AlarmItem(
            0L,
            0L,
            "Preview",
            1721730918345,
            AlarmMode.DayOfWeekAndTime,
            enable = true,
            selectedDaysOfWeek = listOf(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday)
        )
        val alarmItemPreview3 = AlarmItem(
            0L,
            0L,
            "Preview",
            1721730918345,
            AlarmMode.DayOfWeekAndTime,
            enable = true,
            selectedDaysOfWeek = DayOfWeek.DayOfWeekHelper.standardWeek()
        )
        val alarmItemPreview4 = AlarmItem(
            0L,
            0L,
            "",
            1721730918345,
            AlarmMode.OnlyTime,
            enable = false
        )
    }
}