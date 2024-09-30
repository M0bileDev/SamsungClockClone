package com.example.samsungclockclone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.data.local.model.AlarmWithAlarmManagerEntity
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmMilliseconds
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.presentation.customs.dragAndDrop.Index
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm_table WHERE id = :alarmId LIMIT 1")
    suspend fun getAlarmAndAlarmManagersById(
        alarmId: Long
    ): AlarmWithAlarmManagerEntity

    @Query("SELECT * FROM alarm_table WHERE id = :alarmId LIMIT 1")
    fun collectAlarmAndAlarmManagersById(
        alarmId: Long
    ): Flow<AlarmWithAlarmManagerEntity>

    @Query("SELECT * FROM alarm_table")
    suspend fun getAllAlarmAndAlarmManagers(): List<AlarmWithAlarmManagerEntity>

    @Query("SELECT * FROM alarm_table")
    fun collectAllAlarmAndAlarmManagers(): Flow<List<AlarmWithAlarmManagerEntity>>

    @Query("SELECT * FROM alarm_table ORDER BY customOrder ASC")
    fun collectAllAlarmAndAlarmManagersCustomOrder(): Flow<List<AlarmWithAlarmManagerEntity>>

    @Query("SELECT * FROM alarm_table")
    fun collectAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_manager_table ORDER BY fireTime ASC LIMIT 1")
    fun collectNearestAlarmManager(): Flow<AlarmManagerEntity>

    @Insert
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarms(list: List<AlarmEntity>)

    @Query("UPDATE alarm_table SET customOrder = :customOrder WHERE id = :alarmId")
    suspend fun updateAlarmCustomOrder(alarmId: AlarmId, customOrder: Index)

    @Query("UPDATE alarm_table SET mode = :alarmMode WHERE id = :alarmId")
    suspend fun updateAlarmMode(alarmId: AlarmId, alarmMode: AlarmMode)

    @Transaction
    suspend fun updateAlarmCustomOrderList(customOrderList: List<Pair<AlarmId, Index>>) {
        customOrderList.forEach {
            val (alarmId, customOrder) = it
            updateAlarmCustomOrder(alarmId, customOrder)
        }
    }

    @Transaction
    suspend fun insertAlarmUpdateOrder(alarmEntity: AlarmEntity): Long {
        val id = insertAlarm(alarmEntity)
        val updatedAlarmEntity = alarmEntity.copy(id = id, customOrder = id)
        updateAlarm(updatedAlarmEntity)
        return id
    }

    @Transaction
    suspend fun deleteAllAlarms(alarms: List<AlarmEntity>) {
        alarms.forEach { alarm ->
            deleteAlarm(alarm)
        }
    }

    @Delete
    suspend fun deleteAlarm(alarmEntity: AlarmEntity)

    @Insert
    suspend fun insertAlarmManager(alarmManagerEntity: AlarmManagerEntity): Long

    @Transaction
    suspend fun insertAlarmMangers(managers: List<AlarmManagerEntity>): List<Pair<AlarmId, AlarmMilliseconds>> {
        return managers.map { alarmManager ->
            val alarmId = insertAlarmManager(alarmManager)
            alarmId to alarmManager.fireTime
        }
    }

    @Delete
    suspend fun deleteAlarmManager(alarmManagerEntity: AlarmManagerEntity)

    @Query("DELETE FROM alarm_manager_table WHERE parentId = :parentId")
    suspend fun deleteAlarmManagerById(parentId: AlarmId)

    @Query("SELECT id FROM alarm_table WHERE enable == 0")
    suspend fun getDisabledAlarmIds(): List<AlarmId>

    @Query("SELECT * FROM alarm_manager_table WHERE fireTime < :actualMillis AND parentId = :parentId")
    suspend fun getAlarmManagersOutOfDateById(
        parentId: AlarmId,
        actualMillis: Long
    ): List<AlarmManagerEntity>

    @Transaction
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

    @Query("SELECT * FROM alarm_manager_table WHERE parentId = :parentId")
    suspend fun getAlarmManagersById(
        parentId: AlarmId
    ): List<AlarmManagerEntity>

    @Query("UPDATE alarm_manager_table SET fireTime = :newMillis WHERE uniqueId = :managerId")
    suspend fun updateAlarmManagerOutOfDate(managerId: Long, newMillis: Long)

    @Transaction
    suspend fun updateAlarmManagersOutOfDate(pairsIdMillis: List<Pair<Long, Long>>) {
        pairsIdMillis.forEach { pair ->
            val (managerId, newMillis) = pair
            updateAlarmManagerOutOfDate(managerId, newMillis)
        }
    }
}