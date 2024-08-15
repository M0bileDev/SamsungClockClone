package com.example.samsungclockclone.data.local.converter

import androidx.room.TypeConverter
import com.example.samsungclockclone.domain.utils.DayOfWeek

class DayOfWeekConverter {

    @TypeConverter
    fun fromStringToDayOfWeek(string: String) = DayOfWeek.valueOf(string)

    @TypeConverter
    fun fromDayOfWeekToString(dayOfWeek: DayOfWeek) = dayOfWeek.name
}