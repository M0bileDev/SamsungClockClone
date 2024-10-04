package com.example.samsungclockclone.framework.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.framework.receiver.AlarmReceiver
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_MANAGER_ID
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.domain.`typealias`.AlarmMilliseconds
import com.example.samsungclockclone.framework.ext.checkPermission
import com.example.samsungclockclone.usecase.scheduler.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : AlarmScheduler {

    override fun schedule(
        alarmId: AlarmId,
        alarmManagerIdMillisecondsPairs: List<Pair<AlarmManagerId, AlarmMilliseconds>>,
        onScheduleCompleted: () -> Unit,
        onScheduleDenied: () -> Unit
    ) {
        alarmManager.checkPermission(
            onPermissionGranted = {
                alarmManagerIdMillisecondsPairs.forEach { alarmManager ->
                    val intent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra(ALARM_MANAGER_ID, alarmManager.first)
                        putExtra(ALARM_MANAGER_ID, alarmManager.first)
                    }
                    val pendingIntentAlarm = PendingIntent.getBroadcast(
                        context,
                        //unique alarmManager entity id
                        alarmManager.first.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(
                        alarmManager.second,
                        pendingIntentAlarm
                    )
                    this.alarmManager.setAlarmClock(
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
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(
            broadcastReceiver
        )
    }
}