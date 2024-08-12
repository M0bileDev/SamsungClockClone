package com.example.samsungclockclone.di

import com.example.samsungclockclone.data.local.preferences.AlarmPreferencesImpl
import com.example.samsungclockclone.data.local.scheduler.AlarmSchedulerImpl
import com.example.samsungclockclone.domain.preferences.AlarmPreferences
import com.example.samsungclockclone.domain.scheduler.AlarmScheduler
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
}