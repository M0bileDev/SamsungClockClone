package com.example.samsungclockclone.presentation.addAlarm

import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.standardWeek

data class AddAlarmUiState(
    val scheduleInfo: String = "",
    //May change, depend of the settings configuration
    val daysOfWeek: List<DayOfWeek> = standardWeek(),
    val selectedDaysOfWeek: List<DayOfWeek> = emptyList(),
    val alarmName: String = "",
    val soundName: String = "",
    val soundEnabled: Boolean = false,
    val vibrationName: String = "",
    val vibrationEnabled: Boolean = false,
    val snoozeIntervalName: String = "",
    val snoozeRepeatName: String = "",
    val snoozeEnabled: Boolean = false
) {
    companion object AddAlarmUiStateHelper {
        val alarmUiStatePreview = AddAlarmUiState(
            scheduleInfo = "Tomorrow-Sat, 6 Jul",
            daysOfWeek = DayOfWeek.DayOfWeekHelper.standardWeek(),
            selectedDaysOfWeek = listOf(
                DayOfWeek.Monday(),
                DayOfWeek.Saturday(),
                DayOfWeek.Sunday()
            ),
            alarmName = "Alarm 1",
            soundName = "Default",
            soundEnabled = true,
            vibrationName = "Default",
            vibrationEnabled = true,
            snoozeIntervalName = "5 minutes",
            snoozeRepeatName = "3 times",
            snoozeEnabled = true
        )
    }
}