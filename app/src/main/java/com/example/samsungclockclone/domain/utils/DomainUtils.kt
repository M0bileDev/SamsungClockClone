package com.example.samsungclockclone.domain.utils

fun AlarmMode.toAlarmRepeat() =
    when (this) {
        AlarmMode.OnlyTime -> AlarmRepeat.EveryDay
        AlarmMode.DayOfWeekAndTime -> AlarmRepeat.EveryWeek
        AlarmMode.CalendarDateAndTime -> AlarmRepeat.EveryDay
    }
