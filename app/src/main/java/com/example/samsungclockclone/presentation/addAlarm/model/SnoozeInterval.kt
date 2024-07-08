package com.example.samsungclockclone.presentation.addAlarm.model

sealed class SnoozeInterval(
    val minutes: Int
) {
    data object FiveMinutes : SnoozeInterval(5)
    data object TenMinutes : SnoozeInterval(10)
    data object FifteenMinutes : SnoozeInterval(15)
    data object ThirtyMinutes : SnoozeInterval(30)
    data class Custom(val customMinutes: Int) : SnoozeInterval(customMinutes)
}