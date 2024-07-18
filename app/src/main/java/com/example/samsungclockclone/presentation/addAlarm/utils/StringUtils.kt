package com.example.samsungclockclone.presentation.addAlarm.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.samsungclockclone.domain.model.AddAlarmString
import com.example.samsungclockclone.domain.model.DayOfWeek
import com.example.samsungclockclone.domain.model.NameResource
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun AddAlarmString.toStringResource(): String = when (type) {
    com.example.samsungclockclone.domain.model.AddAlarmStringType.TodayX -> {
        stringResource(id = strings.today_x, stringValue)
    }

    com.example.samsungclockclone.domain.model.AddAlarmStringType.TomorrowX -> {
        stringResource(id = strings.tomorrow_x, stringValue)
    }

    com.example.samsungclockclone.domain.model.AddAlarmStringType.EveryX -> {
        val separator = ", "
        val days = StringBuffer()
        daysOfWeek.forEach { dayOfWeek ->
            val dayRes = dayOfWeek.toIntResource()
            val day = stringResource(id = dayRes)
            val shortDay = day.take(3)
            days.append(shortDay).append(separator)
        }
        //drop last separator
        val formattedDays = days.toString().dropLast(separator.length)

        stringResource(id = strings.every_x, formattedDays)
    }

    com.example.samsungclockclone.domain.model.AddAlarmStringType.Everyday -> {
        stringResource(id = strings.everyday)
    }

    com.example.samsungclockclone.domain.model.AddAlarmStringType.ValueOnly -> {
        stringResource(id = strings.value_only_x, stringValue)
    }

    com.example.samsungclockclone.domain.model.AddAlarmStringType.NotDefined -> {
        throw IllegalStateException()
    }
}

fun DayOfWeek.toIntResource() =
    when (this) {
        DayOfWeek.Monday -> strings.monday
        DayOfWeek.Tuesday -> strings.tuesday
        DayOfWeek.Wednesday -> strings.wednesday
        DayOfWeek.Thursday -> strings.thursday
        DayOfWeek.Friday -> strings.friday
        DayOfWeek.Saturday -> strings.saturday
        DayOfWeek.Sunday -> strings.sunday
    }

fun Int.toDayOfWeek() =
    when (this) {
        strings.monday -> DayOfWeek.Monday
        strings.tuesday -> DayOfWeek.Tuesday
        strings.wednesday -> DayOfWeek.Wednesday
        strings.thursday -> DayOfWeek.Thursday
        strings.friday -> DayOfWeek.Friday
        strings.saturday -> DayOfWeek.Saturday
        strings.sunday -> DayOfWeek.Sunday
        else -> throw IllegalStateException()
    }

fun List<DayOfWeek>.toNameResourceList(): List<NameResource> =
    this.map { it.toIntResource() }.map {
        object : NameResource {
            override val nameResourceValue: Int
                get() = it

        }
    }

fun List<NameResource>.toDayOfWeek(): List<DayOfWeek> =
    this.map { it.nameResourceValue.toDayOfWeek() }


