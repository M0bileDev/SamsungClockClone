package com.example.samsungclockclone.domain.model.addAlarm

sealed class SnoozeRepeat(
    val repeats: Int
) {
    data object ThreeTimes : SnoozeRepeat(3)
    data object FiveTimes : SnoozeRepeat(5)
    data object Forever : SnoozeRepeat(Int.MAX_VALUE)
    data class Custom(val customSnooze: Int) : SnoozeRepeat(customSnooze)

}