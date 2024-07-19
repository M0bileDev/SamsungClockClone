package com.example.samsungclockclone.domain.model

import com.example.samsungclockclone.domain.utils.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.utils.AddAlarmStringType

data class AddAlarmString(
    val type: AddAlarmStringType = AddAlarmStringType.NotDefined,
    val stringValue: String = "",
    val daysOfWeek: List<DayOfWeek> = emptyList()
)