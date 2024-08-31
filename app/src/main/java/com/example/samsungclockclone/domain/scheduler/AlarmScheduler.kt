package com.example.samsungclockclone.domain.scheduler

import com.example.samsungclockclone.domain.utils.AlarmId
import com.example.samsungclockclone.domain.utils.AlarmMilliseconds

interface AlarmScheduler {
    fun schedule(
        alarmIdMillisecondsPairs: List<Pair<AlarmId, AlarmMilliseconds>>,
        onScheduleCompleted: () -> Unit,
        onScheduleDenied: () -> Unit
    )

    fun cancel(id: Long)
}