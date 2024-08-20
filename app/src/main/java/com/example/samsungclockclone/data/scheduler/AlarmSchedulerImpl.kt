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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : AlarmScheduler {

    override fun schedule(
        alarms: List<Pair<AlarmId, AlarmMilliseconds>>,
        onScheduleCompleted: () -> Unit,
        onScheduleDenied: () -> Unit
    ) {
        checkPermission(
            onPermissionGranted = {
                alarms.forEach { alarm ->
                    val intent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra(ALARM_ID_KEY, alarm.first)
                    }
                    val broadcastReceiver = PendingIntent.getBroadcast(
                        context,
                        alarm.first.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        alarm.second,
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

    private fun checkPermission(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val result = alarmManager.canScheduleExactAlarms()
            if (result) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    }
}