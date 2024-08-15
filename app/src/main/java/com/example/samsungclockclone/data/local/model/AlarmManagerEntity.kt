package com.example.samsungclockclone.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.samsungclockclone.domain.utils.AlarmRepeat
import com.example.samsungclockclone.domain.utils.DayOfWeek

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
    @PrimaryKey(autoGenerate = true)
    val uniqueId: Long = 0L,
    val parentId: Long,
    val fireTime: Long,
    val dayOfWeek: DayOfWeek? = null,
    val repeat: AlarmRepeat
)