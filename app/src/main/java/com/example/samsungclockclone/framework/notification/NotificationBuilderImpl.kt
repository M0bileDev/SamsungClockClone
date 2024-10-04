package com.example.samsungclockclone.framework.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.NotificationId
import com.example.samsungclockclone.framework.receiver.AlarmDismissReceiver
import com.example.samsungclockclone.framework.receiver.AlarmDismissReceiver.Companion.NOTIFICATION_ID
import com.example.samsungclockclone.framework.utils.drawables
import com.example.samsungclockclone.framework.utils.strings
import com.example.samsungclockclone.presentation.main.MainActivity
import com.example.samsungclockclone.presentation.screens.dismissAlarm.DismissAlarmActivity
import com.example.samsungclockclone.usecase.notification.NotificationBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationBuilderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationBuilder {

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
        const val OPEN_MAIN_ACTIVITY_REQUEST_CODE = 0
        const val OPEN_DISMISS_ALARM_ACTIVITY_REQUEST_CODE = 1
    }

    override fun createAlarmNotificationChannel() = with(context) {
        val name = getString(strings.notification_name_alarm_and_timer)
        val description = getString(strings.notification_description_name_alarm_and_timer)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val alarmChannel = NotificationChannel(ALARM_CHANNEL_ID, name, importance).apply {
            this.description = description
        }
        notificationManager.createNotificationChannel(alarmChannel)
    }

    override fun sendAlarmNotification(id: NotificationId, description: String) = with(context) {

        val intentDismissAlarm = Intent(this, AlarmDismissReceiver::class.java).apply {
            putExtra(NOTIFICATION_ID, id)
        }
        val pendingIntentDismissAlarm =
            PendingIntent.getBroadcast(
                this,
                //unique notification id
                id.toInt(),
                intentDismissAlarm,
                PendingIntent.FLAG_IMMUTABLE
            )

        val intentOpenMainActivity = Intent(this, MainActivity::class.java)
        val pendingIntentOpenMainActivity = PendingIntent.getActivity(
            this,
            OPEN_MAIN_ACTIVITY_REQUEST_CODE,
            intentOpenMainActivity,
            PendingIntent.FLAG_IMMUTABLE
        )

        val intentFullScreen = Intent(this, DismissAlarmActivity::class.java).apply {
            putExtra(NOTIFICATION_ID, id)
        }
        val pendingIntentFullScreen = PendingIntent.getActivity(
            this,
            OPEN_DISMISS_ALARM_ACTIVITY_REQUEST_CODE,
            intentFullScreen,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon(drawables.ic_launcher_foreground)
            .setContentTitle(getString(strings.app_name))
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntentOpenMainActivity)
            .setFullScreenIntent(pendingIntentFullScreen, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                drawables.ic_alarm_off,
                getString(strings.dismiss),
                pendingIntentDismissAlarm
            )

        notificationManager.notify(id.toInt(), notificationBuilder.build())
    }

    override fun cancelAlarmNotification(id: AlarmId) = notificationManager.cancel(id.toInt())

    //TODO playing ringtone or vibration
    //todo add logic to cancel alarm and notification when user dismiss notification
    //TODO create notification group
}