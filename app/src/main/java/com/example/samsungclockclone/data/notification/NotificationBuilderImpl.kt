package com.example.samsungclockclone.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.example.samsungclockclone.domain.notification.NotificationBuilder
import com.example.samsungclockclone.domain.utils.AlarmId
import com.example.samsungclockclone.ui.utils.drawables
import com.example.samsungclockclone.ui.utils.strings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationBuilderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationBuilder {

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
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

    override fun sendAlarmNotification(id: AlarmId, description: String) = with(context) {
        val notificationBuilder = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon(drawables.ic_launcher_foreground)
            .setContentTitle(getString(strings.app_name))
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        notificationManager.notify(id.toInt(), notificationBuilder.build())
    }

    override fun cancelAlarmNotification(id: AlarmId) = notificationManager.cancel(id.toInt())

    //TODO add dismiss action
    //TODO start an activity from notification
}