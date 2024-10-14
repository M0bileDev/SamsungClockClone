package com.example.samsungclockclone.data.dataSource.local

import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.data.local.model.AlarmWithAlarmManagerEntity
import com.example.samsungclockclone.data.local.model.NotificationAlarm
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.domain.`typealias`.AlarmMilliseconds
import com.example.samsungclockclone.presentation.customs.dragAndDrop.Index
import kotlinx.coroutines.flow.Flow

interface DatabaseSource {

    suspend fun getAlarmAndAlarmManagersById(
        alarmId: Long
    ): AlarmWithAlarmManagerEntity

    fun collectAlarmAndAlarmManagersById(
        alarmId: Long
    ): Flow<AlarmWithAlarmManagerEntity>

    suspend fun getAllAlarmAndAlarmManagers(): List<AlarmWithAlarmManagerEntity>

    fun collectAllAlarmAndAlarmManagers(): Flow<List<AlarmWithAlarmManagerEntity>>

    fun collectAllAlarmAndAlarmManagersCustomOrder(): Flow<List<AlarmWithAlarmManagerEntity>>

    fun collectAllAlarms(): Flow<List<AlarmEntity>>

    fun collectNearestAlarmManager(): Flow<AlarmManagerEntity>

    suspend fun insertAlarm(alarm: AlarmEntity): Long

    suspend fun updateAlarm(alarm: AlarmEntity)

    suspend fun updateAlarms(list: List<AlarmEntity>)

    suspend fun updateAlarmCustomOrder(alarmId: AlarmId, customOrder: Index)

    suspend fun updateAlarmMode(alarmId: AlarmId, alarmMode: AlarmMode)

    suspend fun updateAlarmCustomOrderList(customOrderList: List<Pair<AlarmId, Index>>) {
        customOrderList.forEach {
            val (alarmId, customOrder) = it
            updateAlarmCustomOrder(alarmId, customOrder)
        }
    }

    suspend fun insertAlarmUpdateOrder(alarmEntity: AlarmEntity): Long {
        val id = insertAlarm(alarmEntity)
        val updatedAlarmEntity = alarmEntity.copy(id = id, customOrder = id)
        updateAlarm(updatedAlarmEntity)
        return id
    }

    suspend fun deleteAllAlarms(alarms: List<AlarmEntity>) {
        alarms.forEach { alarm ->
            deleteAlarm(alarm)
        }
    }

    suspend fun deleteAlarm(alarmEntity: AlarmEntity)

    suspend fun insertAlarmManager(alarmManagerEntity: AlarmManagerEntity): Long

    suspend fun insertAlarmMangers(managers: List<AlarmManagerEntity>): List<Pair<AlarmId, AlarmMilliseconds>> {
        return managers.map { alarmManager ->
            val alarmId = insertAlarmManager(alarmManager)
            alarmId to alarmManager.fireTime
        }
    }

    suspend fun deleteAlarmManager(alarmManagerEntity: AlarmManagerEntity)

    suspend fun deleteAlarmManagerById(parentId: AlarmId)

    suspend fun getDisabledAlarmIds(): List<AlarmId>

    suspend fun getAlarmManagersOutOfDateById(
        parentId: AlarmId,
        actualMillis: Long
    ): List<AlarmManagerEntity>

    suspend fun getAlarmManagersOutOfDateByIds(
        parentIds: List<AlarmId>,
        actualMillis: Long
    ): List<AlarmManagerEntity> {
        return parentIds.map { parentId ->
            getAlarmManagersOutOfDateById(
                parentId, actualMillis
            )
        }.flatten()
    }

    suspend fun getAlarmManagerById(
        alarmManagerId: AlarmManagerId
    ): AlarmManagerEntity

    suspend fun getAlarmManagersById(
        parentId: AlarmId
    ): List<AlarmManagerEntity>

    suspend fun updateAlarmManagerOutOfDate(managerId: Long, newMillis: Long)

    suspend fun updateAlarmManagersOutOfDate(pairsIdMillis: List<Pair<Long, Long>>) {
        pairsIdMillis.forEach { pair ->
            val (managerId, newMillis) = pair
            updateAlarmManagerOutOfDate(managerId, newMillis)
        }
    }

    suspend fun getNotificationAlarm(alarmId: AlarmId, alarmManagerId: AlarmManagerId): NotificationAlarm

}