package com.example.samsungclockclone.framework.dataSource

import com.example.samsungclockclone.data.dataSource.local.DatabaseSource
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.data.local.model.AlarmWithAlarmManagerEntity
import com.example.samsungclockclone.data.local.model.NotificationAlarm
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.presentation.customs.dragAndDrop.Index
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseSourceImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : DatabaseSource {

    override suspend fun getAlarmAndAlarmManagersById(alarmId: Long): AlarmWithAlarmManagerEntity =
        alarmDao.getAlarmAndAlarmManagersById(alarmId)

    override fun collectAlarmAndAlarmManagersById(alarmId: Long): Flow<AlarmWithAlarmManagerEntity> =
        alarmDao.collectAlarmAndAlarmManagersById(alarmId)

    override suspend fun getAllAlarmAndAlarmManagers(): List<AlarmWithAlarmManagerEntity> =
        alarmDao.getAllAlarmAndAlarmManagers()

    override fun collectAllAlarmAndAlarmManagers(): Flow<List<AlarmWithAlarmManagerEntity>> =
        alarmDao.collectAllAlarmAndAlarmManagers()

    override fun collectAllAlarmAndAlarmManagersCustomOrder(): Flow<List<AlarmWithAlarmManagerEntity>> =
        alarmDao.collectAllAlarmAndAlarmManagersCustomOrder()

    override fun collectAllAlarms(): Flow<List<AlarmEntity>> = alarmDao.collectAllAlarms()

    override fun collectNearestAlarmManager(): Flow<AlarmManagerEntity> =
        alarmDao.collectNearestAlarmManager()

    override suspend fun insertAlarm(alarm: AlarmEntity): Long = alarmDao.insertAlarm(alarm)

    override suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)

    override suspend fun updateAlarms(list: List<AlarmEntity>) = alarmDao.updateAlarms(list)

    override suspend fun updateAlarmCustomOrder(alarmId: AlarmId, customOrder: Index) =
        alarmDao.updateAlarmCustomOrder(alarmId, customOrder)

    override suspend fun updateAlarmMode(alarmId: AlarmId, alarmMode: AlarmMode) =
        alarmDao.updateAlarmMode(alarmId, alarmMode)

    override suspend fun deleteAlarm(alarmEntity: AlarmEntity) = alarmDao.deleteAlarm(alarmEntity)

    override suspend fun insertAlarmManager(alarmManagerEntity: AlarmManagerEntity): Long =
        alarmDao.insertAlarmManager(alarmManagerEntity)

    override suspend fun deleteAlarmManager(alarmManagerEntity: AlarmManagerEntity) =
        alarmDao.deleteAlarmManager(alarmManagerEntity)

    override suspend fun deleteAlarmManagerById(parentId: AlarmId) =
        alarmDao.deleteAlarmManagerById(parentId)

    override suspend fun getDisabledAlarmIds(): List<AlarmId> = alarmDao.getDisabledAlarmIds()

    override suspend fun getAlarmManagersOutOfDateById(
        parentId: AlarmId,
        actualMillis: Long
    ): List<AlarmManagerEntity> = alarmDao.getAlarmManagersOutOfDateById(parentId, actualMillis)

    override suspend fun getAlarmManagerById(alarmManagerId: AlarmManagerId): AlarmManagerEntity =
        alarmDao.getAlarmManagerById(alarmManagerId)

    override suspend fun getAlarmManagersById(parentId: AlarmId): List<AlarmManagerEntity> =
        alarmDao.getAlarmManagersById(parentId)

    override suspend fun updateAlarmManagerOutOfDate(managerId: Long, newMillis: Long) =
        alarmDao.updateAlarmManagerOutOfDate(managerId, newMillis)

    override suspend fun updateAlarmOngoingById(alarmId: AlarmId, ongoing: Boolean) =
        alarmDao.updateAlarmOngoingById(alarmId, ongoing)

    override suspend fun getNotificationAlarm(
        alarmId: AlarmId,
        alarmManagerId: AlarmManagerId
    ): NotificationAlarm =
        alarmDao.getNotificationAlarm(alarmId, alarmManagerId)

}