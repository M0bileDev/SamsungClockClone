package com.example.samsungclockclone.data.local.scheduler

interface AlarmScheduler {
    fun schedule(id: Long, triggerAtMillis: Long)
    fun cancel(id: Long)
}