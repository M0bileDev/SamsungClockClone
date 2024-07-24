package com.example.samsungclockclone.presentation.addAlarm

import com.example.samsungclockclone.domain.model.addAlarm.AddAlarmString
import com.example.samsungclockclone.domain.utils.DayOfWeek
import com.example.samsungclockclone.domain.utils.DayOfWeek.DayOfWeekHelper.standardWeek
import com.example.samsungclockclone.presentation.addAlarm.utils.AddAlarmStringType

// TODO: Addd Immutable or Stable annotation, test performance
data class AddAlarmUiState(
    val addAlarmString: AddAlarmString = AddAlarmString(),
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
    val snoozeEnabled: Boolean = false,
    val displayPermissionRequire: Boolean = false,
    val displayDatePicker: Boolean = false
) {
    companion object AddAlarmUiStateHelper {
        val addAlarmUiStatePreview = AddAlarmUiState(
            addAlarmString = AddAlarmString(type = AddAlarmStringType.Everyday),
            daysOfWeek = standardWeek(),
            selectedDaysOfWeek = listOf(
                DayOfWeek.Monday,
                DayOfWeek.Saturday,
                DayOfWeek.Sunday
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