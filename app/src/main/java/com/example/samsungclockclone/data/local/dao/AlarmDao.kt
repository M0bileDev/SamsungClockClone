package com.example.samsungclockclone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.data.local.model.AlarmWithAlarmManagerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm_table WHERE id =:alarmId")
    fun getAlarmAndAlarmManagersById(
        alarmId: Long
    ): Flow<List<AlarmWithAlarmManagerEntity>>

    @Query("SELECT * FROM alarm_table")
    fun getAlarmAndAlarmManagers(): Flow<List<AlarmWithAlarmManagerEntity>>

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_manager_table ORDER BY fireTime ASC LIMIT 1")
    fun getNearestAlarmManager(): Flow<AlarmManagerEntity>

    @Insert
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarmEntity: AlarmEntity)

    @Insert
    suspend fun insertAlarmManager(alarmManagerEntity: AlarmManagerEntity) : Long

    @Delete
    suspend fun deleteAlarmManager(alarmManagerEntity: AlarmManagerEntity)

}