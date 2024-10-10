package com.example.samsungclockclone.di

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.framework.dataSource.DatabaseSourceImpl
import com.example.samsungclockclone.framework.dialog.DialogListenerImpl
import com.example.samsungclockclone.framework.notification.NotificationBuilderImpl
import com.example.samsungclockclone.framework.permissions.PermissionsListenerImpl
import com.example.samsungclockclone.framework.preferences.AlarmPreferencesImpl
import com.example.samsungclockclone.framework.preferences.SelectionPreferencesImpl
import com.example.samsungclockclone.framework.ringtone.RingtoneControllerImpl
import com.example.samsungclockclone.framework.scheduler.AlarmSchedulerImpl
import com.example.samsungclockclone.framework.ticker.TimeTickerImpl
import com.example.samsungclockclone.usecase.dialog.DialogListener
import com.example.samsungclockclone.usecase.notification.NotificationBuilder
import com.example.samsungclockclone.usecase.permissions.PermissionsListener
import com.example.samsungclockclone.usecase.preferences.AlarmPreferences
import com.example.samsungclockclone.usecase.preferences.SelectionPreferences
import com.example.samsungclockclone.usecase.ringtone.RingtoneController
import com.example.samsungclockclone.usecase.scheduler.AlarmScheduler
import com.example.samsungclockclone.usecase.ticker.TimeTicker
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

    @[Binds Singleton]
    abstract fun bindDatabaseSource(databaseSource: DatabaseSourceImpl): DatabaseSource

    @[Binds Singleton]
    abstract fun bindRingtoneController(ringtoneController: RingtoneControllerImpl): RingtoneController
}