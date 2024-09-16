package com.example.samsungclockclone.di

import com.example.samsungclockclone.data.local.dialog.DialogListenerImpl
import com.example.samsungclockclone.data.local.permissions.PermissionsListenerImpl
import com.example.samsungclockclone.data.local.preferences.AlarmPreferencesImpl
import com.example.samsungclockclone.data.local.preferences.SelectionPreferencesImpl
import com.example.samsungclockclone.data.scheduler.AlarmSchedulerImpl
import com.example.samsungclockclone.data.ticker.TimeTickerImpl
import com.example.samsungclockclone.domain.dialog.DialogListener
import com.example.samsungclockclone.domain.permissions.PermissionsListener
import com.example.samsungclockclone.domain.preferences.AlarmPreferences
import com.example.samsungclockclone.domain.preferences.SelectionPreferences
import com.example.samsungclockclone.domain.scheduler.AlarmScheduler
import com.example.samsungclockclone.domain.ticker.TimeTicker
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
}