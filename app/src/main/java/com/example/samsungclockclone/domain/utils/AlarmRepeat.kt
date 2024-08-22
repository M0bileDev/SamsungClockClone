package com.example.samsungclockclone.domain.utils

enum class AlarmRepeat {
    EveryWeek,
    EveryDay;

    companion object {
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
}

