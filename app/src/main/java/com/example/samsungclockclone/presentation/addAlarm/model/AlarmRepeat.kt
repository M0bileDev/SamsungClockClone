package com.example.samsungclockclone.presentation.addAlarm.model

enum class AlarmRepeat {
    EveryWeek,
    EveryDay;

    fun String.fromStringToAlarmRepeat(): AlarmRepeat {
        return AlarmRepeat.entries.find { it.name == this }
            ?: throw IllegalStateException("Provided name not exists in AlarmRepeat enum class!")
    }

    fun AlarmRepeat.createRepeatMillis(): Long {
        return when (this) {
            EveryWeek -> (((24 * 7) * 60) * 60) * 1000
            EveryDay -> ((24 * 60) * 60) * 1000
        }
    }
}

