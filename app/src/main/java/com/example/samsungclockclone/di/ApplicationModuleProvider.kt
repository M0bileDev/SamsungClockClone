package com.example.samsungclockclone.di

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.db.AlarmDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
object ApplicationModuleProvider {

    @[Provides Singleton]
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @[Provides Singleton]
    fun provideAlarmDatabase(
        @ApplicationContext context: Context
    ): AlarmDatabase {
        return Room.databaseBuilder(
            context,
            AlarmDatabase::class.java,
            "alarm-database"
        ).build()
    }

    @[Provides Singleton]
    fun provideAlarmDao(
        alarmDatabase: AlarmDatabase
    ): AlarmDao {
        return alarmDatabase.alarmDao()
    }

}