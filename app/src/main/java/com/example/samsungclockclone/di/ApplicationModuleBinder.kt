package com.example.samsungclockclone.di

import com.example.samsungclockclone.framework.dialog.DialogListenerImpl
import com.example.samsungclockclone.framework.permissions.PermissionsListenerImpl
import com.example.samsungclockclone.framework.preferences.AlarmPreferencesImpl
import com.example.samsungclockclone.framework.preferences.SelectionPreferencesImpl
import com.example.samsungclockclone.framework.notification.NotificationBuilder
import com.example.samsungclockclone.framework.notification.NotificationBuilderImpl
import com.example.samsungclockclone.framework.scheduler.AlarmSchedulerImpl
import com.example.samsungclockclone.framework.ticker.TimeTickerImpl
import com.example.samsungclockclone.framework.dialog.DialogListener
import com.example.samsungclockclone.framework.permissions.PermissionsListener
import com.example.samsungclockclone.framework.preferences.AlarmPreferences
import com.example.samsungclockclone.framework.preferences.SelectionPreferences
import com.example.samsungclockclone.framework.scheduler.AlarmScheduler
import com.example.samsungclockclone.framework.ticker.TimeTicker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
abstract class ApplicationModuleBinder {

    @[Binds Singleton]
    abstract fun bindAlarmScheduler(alarmScheduler: AlarmSchedulerImpl): AlarmScheduler

    @[Binds Singleton]
    abstract fun bindAlarmPreferences(alarmPreferences: AlarmPreferencesImpl): AlarmPreferences

    @[Binds Singleton]
    abstract fun bindTimeTicker(timeTicker: TimeTickerImpl): TimeTicker

    @[Binds Singleton]
    abstract fun bindSelectionPreferences(selectionPreferences: SelectionPreferencesImpl): SelectionPreferences

    @[Binds Singleton]
    abstract fun bindDialogListener(dialogListener: DialogListenerImpl): DialogListener

    @[Binds Singleton]
    abstract fun bindPermissionsListener(permissionsListener: PermissionsListenerImpl): PermissionsListener

    @[Binds Singleton]
    abstract fun bindNotificationBuilder(notificationBuilder: NotificationBuilderImpl): NotificationBuilder
}