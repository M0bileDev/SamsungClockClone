package com.example.samsungclockclone.data.local.scheduler

typealias AlarmId = Long
typealias AlarmMilliseconds = Long

interface AlarmScheduler {
    fun schedule(
        alarms: List<Pair<AlarmId, AlarmMilliseconds>>,
        onScheduleCompleted: () -> Unit,
        onScheduleDenied: () -> Unit
    )

    fun cancel(id: Long)
}