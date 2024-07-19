package com.example.samsungclockclone.data.local.converter

import androidx.room.TypeConverter
import com.example.samsungclockclone.domain.utils.AlarmMode

class AlarmModeConverter {
    @TypeConverter
    fun fromStringToAlarmMode(string: String) = AlarmMode.valueOf(string)

    @TypeConverter
    fun fromAlarmModeToString(alarmMode: AlarmMode) = alarmMode.name
}