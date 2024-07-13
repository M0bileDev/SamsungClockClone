package com.example.samsungclockclone.di

import com.example.samsungclockclone.data.local.scheduler.AlarmScheduler
import com.example.samsungclockclone.data.local.scheduler.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
abstract class ApplicationModuleBinder {

    @[Binds Singleton]
    abstract fun bindAlarmScheduler(alarmScheduler: AlarmSchedulerImpl): AlarmScheduler
}