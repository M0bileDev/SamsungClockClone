package com.example.samsungclockclone.domain.ext

import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.model.AlarmRepeat

fun AlarmMode.toAlarmRepeat() =
    when (this) {
        AlarmMode.OnlyTime -> AlarmRepeat.EveryDay
        AlarmMode.DayOfWeekAndTime -> AlarmRepeat.EveryWeek
        AlarmMode.CalendarDateAndTime -> AlarmRepeat.EveryDay
    }