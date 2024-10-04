package com.example.samsungclockclone.usecase.scheduler

import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmMilliseconds

interface AlarmScheduler {
    fun schedule(
        alarmIdMillisecondsPairs: List<Pair<AlarmId, AlarmMilliseconds>>,
        onScheduleCompleted: () -> Unit,
        onScheduleDenied: () -> Unit
    )

    fun cancel(alarmId: AlarmId)
}