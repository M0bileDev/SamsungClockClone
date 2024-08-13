package com.example.samsungclockclone.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.samsungclockclone.domain.model.AlarmOrder
import com.example.samsungclockclone.domain.preferences.AlarmPreferences
import com.example.samsungclockclone.ext.alarmDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmPreferencesImpl @Inject constructor(
    @ApplicationContext val context: Context
) : AlarmPreferences {

    companion object {
        const val ORDER_KEY = "ORDER_KEY"
    }

    private val alarmDataStoreOrderKey = stringPreferencesKey(ORDER_KEY)
    override suspend fun saveAlarmOrder(alarmOrder: AlarmOrder) {
        context.alarmDataStore.edit { alarmPref ->
            alarmPref[alarmDataStoreOrderKey] = alarmOrder.name
        }
    }

    override suspend fun collectAlarmOrder(): Flow<AlarmOrder> {
        return context.alarmDataStore.data.map { alarmPref ->
            alarmPref[alarmDataStoreOrderKey]?.let { name ->
                AlarmOrder.valueOf(name)
            } ?: AlarmOrder.DEFAULT
        }
    }

}