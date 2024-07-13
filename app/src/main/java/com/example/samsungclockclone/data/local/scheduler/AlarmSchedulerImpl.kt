package com.example.samsungclockclone.data.local.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.data.local.receiver.ALARM_ID_KEY
import com.example.samsungclockclone.data.local.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : AlarmScheduler {

    override fun schedule(id: Long, triggerAtMillis: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_ID_KEY, id)
        }
        val broadcastReceiver = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis,
            broadcastReceiver
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