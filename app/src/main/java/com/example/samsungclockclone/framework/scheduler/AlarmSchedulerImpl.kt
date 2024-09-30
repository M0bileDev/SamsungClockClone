package com.example.samsungclockclone.framework.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.framework.receiver.AlarmReceiver
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_ID
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmMilliseconds
import com.example.samsungclockclone.framework.ext.checkPermission
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
                        putExtra(ALARM_ID, alarm.first)
                    }
                    val pendingIntentAlarm = PendingIntent.getBroadcast(
                        context,
                        //unique alarm entity id
                        alarm.first.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(
                        alarm.second,
                        pendingIntentAlarm
                    )
                    alarmManager.setAlarmClock(
                        alarmClockInfo,
                        pendingIntentAlarm
                    )
                }.run {
                    onScheduleCompleted()
                }
            },
            onPermissionDenied = onScheduleDenied
        )

    }

    override fun cancel(alarmId: AlarmId) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val broadcastReceiver = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(
            broadcastReceiver
        )
    }
}