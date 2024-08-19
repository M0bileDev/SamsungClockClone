package com.example.samsungclockclone.domain.model.alarm

data class AlarmDifference(
    val daysDifference: Int,
    val hoursDifference: Int,
    val minutesDifference: Int,
    val differenceType: DifferenceType
)