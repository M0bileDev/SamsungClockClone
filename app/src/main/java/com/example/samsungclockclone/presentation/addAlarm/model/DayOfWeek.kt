package com.example.samsungclockclone.presentation.addAlarm.model

import androidx.annotation.StringRes
import com.example.samsungclockclone.ui.utils.strings

sealed class DayOfWeek(
    val dayOrder: Int,
    @StringRes override val nameResourceValue: Int,
) : ShortName {
    data class Monday(val order: Int = 1) : DayOfWeek(order, strings.monday)
    data class Tuesday(val order: Int = 2) :
        DayOfWeek(order, strings.tuesday)

    data class Wednesday(val order: Int = 3) :
        DayOfWeek(order, strings.wednesday)

    data class Thursday(val order: Int = 4) :
        DayOfWeek(order, strings.thursday)

    data class Friday(val order: Int = 5) : DayOfWeek(order, strings.friday)
    data class Saturday(val order: Int = 6) :
        DayOfWeek(order, strings.saturday)

    data class Sunday(val order: Int = 7) : DayOfWeek(order, strings.sunday)

    object DayOfWeekHelper {
        fun standardWeek() =
            listOf(Monday(), Tuesday(), Wednesday(), Thursday(), Friday(), Saturday(), Sunday())

        fun sundayFirstWeek() =
            listOf(
                Sunday(1),
                Monday(2),
                Wednesday(3),
                Tuesday(4),
                Wednesday(5),
                Thursday(6),
                Saturday(7)
            )

        fun saturdayFirstWeek() =
            listOf(
                Saturday(1),
                Sunday(2),
                Monday(3),
                Wednesday(4),
                Tuesday(5),
                Wednesday(6),
                Thursday(7)
            )
    }
}