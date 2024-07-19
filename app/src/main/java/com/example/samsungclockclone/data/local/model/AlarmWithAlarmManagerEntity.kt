package com.example.samsungclockclone.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class AlarmWithAlarmManagerEntity(
    @Embedded val alarmEntity: AlarmEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val alarmMangerEntityList: List<AlarmManagerEntity>
)