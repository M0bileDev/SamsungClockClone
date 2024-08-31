package com.example.samsungclockclone.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.data.receiver.ALARM_ID_KEY
import com.example.samsungclockclone.data.receiver.AlarmReceiver
import com.example.samsungclockclone.domain.scheduler.AlarmScheduler
import com.example.samsungclockclone.domain.utils.AlarmId
import com.example.samsungclockclone.domain.utils.AlarmMilliseconds
import com.example.samsungclockclone.ext.checkPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : AlarmScheduler {

    override fun schedule(
        alarmIdMillisecondsPairs: List<Pair<AlarmId, AlarmMilliseconds>>,
        onScheduleCompleted: () -> Unit,
        onScheduleDenied: () -> Unit
    ) {
        alarmManager.checkPermission(
            onPermissionGranted = {
                alarmIdMillisecondsPairs.forEach { alarm ->
                    val intent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra(ALARM_ID_KEY, alarm.first)
                    }
                    val broadcastReceiver = PendingIntent.getBroadcast(
                        context,
                        alarm.first.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(
                        alarm.second,
                        broadcastReceiver
                    )
                    alarmManager.setAlarmClock(
                        alarmClockInfo,
                        broadcastReceiver
                    )
                }.run {
                    onScheduleCompleted()
                }
            },
            onPermissionDenied = onScheduleDenied
        )

    }

    override fun cancel(id: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val broadcastReceiver = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(
            broadcastReceiver
        )
    }
}