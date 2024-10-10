package com.example.samsungclockclone.usecase.scheduler

import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.domain.`typealias`.AlarmMilliseconds

interface AlarmScheduler {
    fun schedule(
        alarmId: AlarmId,
        alarmManagerIdMillisecondsPairs: List<Pair<AlarmManagerId, AlarmMilliseconds>>,
        onScheduleCompleted: () -> Unit,
        onScheduleDenied: () -> Unit
    )

    fun cancel(alarmId: AlarmId)
}