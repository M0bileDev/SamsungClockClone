package com.example.samsungclockclone.data.local.converter

import androidx.room.TypeConverter
import com.example.samsungclockclone.domain.model.AlarmRepeat

class AlarmRepeatConverter {

    @TypeConverter
    fun fromStringToAlarmRepeat(string: String) = AlarmRepeat.valueOf(string)

    @TypeConverter
    fun fromAlarmRepeatToString(alarmRepeat: AlarmRepeat) = alarmRepeat.name
}