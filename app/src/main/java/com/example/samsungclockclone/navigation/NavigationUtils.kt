package com.example.samsungclockclone.navigation

import androidx.annotation.StringRes
import com.example.samsungclockclone.ui.utils.strings

sealed class Screens(val route: String, @StringRes val name: Int) {
    data object Alarm : Screens("alarm", strings.alarm)
    data object Stopwatch : Screens("stopwatch", strings.stopwatch)
    data object Timer : Screens("timer", strings.timer)
    data object AddAlarm : Screens("alarm/add", strings.add_alarm)
    data object EditAlarm : Screens("alarm/edit", strings.add_alarm)
}

object NavigationUtils {
    val navBottomItems = listOf(
        Screens.Alarm,
        Screens.Stopwatch,
        Screens.Timer
    )
}