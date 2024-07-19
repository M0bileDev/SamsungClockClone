package com.example.samsungclockclone.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.samsungclockclone.data.local.converter.AlarmModeConverter
import com.example.samsungclockclone.data.local.converter.AlarmRepeatConverter
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity

@Database(
    entities = [
        AlarmEntity::class,
        AlarmManagerEntity::class
    ],
    version = 1
)
@TypeConverters(
    AlarmModeConverter::class,
    AlarmRepeatConverter::class
)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}