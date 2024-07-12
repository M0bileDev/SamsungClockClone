package com.example.samsungclockclone.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarm_manager_table",
    foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("parentId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AlarmManagerEntity(
    @PrimaryKey
    val uniqueId: Long,
    val parentId: Long,
    val fireTime: Long,
    val repeatTime: Long
)