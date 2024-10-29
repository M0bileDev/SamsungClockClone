package com.example.samsungclockclone.framework.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.abstraction.notification.NotificationBuilder
import com.example.samsungclockclone.abstraction.ringtone.RingtoneController
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_ID
import com.example.samsungclockclone.usecase.UpdateOngoingAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmDismissReceiver : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var ringtoneController: RingtoneController

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    @Inject
    lateinit var updateOngoingAlarmUseCase: UpdateOngoingAlarmUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmId = intent?.getLongExtra(ALARM_ID, -1L) ?: -1L

        if (alarmId == -1L) return

        coroutineScope.launch {
            updateOngoingAlarmUseCase(alarmId, parentScope = this)
        }

        ringtoneController.stop()
        notificationBuilder.cancelAlarmNotification(alarmId)
    }
}