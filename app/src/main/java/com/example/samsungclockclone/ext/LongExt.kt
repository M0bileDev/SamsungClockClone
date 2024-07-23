package com.example.samsungclockclone.ext

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toDate(pattern: String): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
    val localDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault()
    )
    return localDateTime.format(dateTimeFormatter)
}