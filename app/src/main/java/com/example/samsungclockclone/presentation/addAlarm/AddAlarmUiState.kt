package com.example.samsungclockclone.presentation.addAlarm

import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.standardWeek

data class AddAlarmUiState(
    //May change, depend of the settings configuration
    val daysOfWeek: List<DayOfWeek> = standardWeek(),

    val selectedDaysOfWeek: List<DayOfWeek> = emptyList(),
    val alarmName: String = "",
    val soundName: String = "Default",
    val soundEnabled: Boolean = true,
    val vibrationName: String = "Default",
    val vibrationEnabled: Boolean = true,
    val snoozeIntervalName: String = "5 minutes",
    val snoozeRepeatName: String = "3 times",
    val snoozeEnabled: Boolean = true
)