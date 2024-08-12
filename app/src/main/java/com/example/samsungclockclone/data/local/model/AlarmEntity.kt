package com.example.samsungclockclone.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.samsungclockclone.domain.utils.AlarmMode

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val customOrder:Long = 0L,
    val mode: AlarmMode,
    val name: String,
    val enable: Boolean
)
