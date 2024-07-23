package com.example.samsungclockclone.domain.model.alarm

import com.example.samsungclockclone.data.local.scheduler.AlarmId
import com.example.samsungclockclone.domain.utils.AlarmMode

data class AlarmItem(
    val alarmId: AlarmId = 0L,
    val name: String = "",
    val fireTime: Long = 0L,
    val mode: AlarmMode = AlarmMode.OnlyTime,
    val enable: Boolean = false
) {
    companion object {
        val alarmItemPreview = AlarmItem(
            0L,
            "Preview",
            1721730918345,
            AlarmMode.OnlyTime,
            enable = true
        )
    }
}