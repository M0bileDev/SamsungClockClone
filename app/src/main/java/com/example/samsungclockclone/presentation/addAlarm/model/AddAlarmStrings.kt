package com.example.samsungclockclone.presentation.addAlarm.model

import com.example.samsungclockclone.ui.utils.strings

enum class AddAlarmStrings(override val nameResourceValue: Int) : NameResource {
    TodayX(strings.today_x),
    TomorrowX(strings.tomorrow_x),
    Everyday(strings.everyday),
    ValueOnly(strings.empty_x)
}

data class AddAlarmStringsValues(
    val resource: NameResource = AddAlarmStrings.ValueOnly,
    val values: List<String> = emptyList()
)