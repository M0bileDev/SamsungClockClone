package com.example.samsungclockclone.domain.model

data class AddAlarmString (
    val type: AddAlarmStringType = AddAlarmStringType.NotDefined,
    val stringValue: String = "",
    val daysOfWeek: List<DayOfWeek> = emptyList()
)