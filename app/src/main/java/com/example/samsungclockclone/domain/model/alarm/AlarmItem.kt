package com.example.samsungclockclone.domain.model.alarm

import com.example.samsungclockclone.domain.utils.AlarmMode

data class AlarmItem(
    val fireTime: Long = 0L,
    val mode: AlarmMode = AlarmMode.OnlyTime,
    val enable: Boolean = false
)