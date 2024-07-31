package com.example.samsungclockclone.domain.utils

enum class DayOfWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    object DayOfWeekHelper {
        fun standardWeek() =
            listOf(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)

        fun sundayFirstWeek() =
            listOf(
                Sunday,
                Monday,
                Wednesday,
                Tuesday,
                Wednesday,
                Thursday,
                Saturday
            )

        fun saturdayFirstWeek() =
            listOf(
                Saturday,
                Sunday,
                Monday,
                Wednesday,
                Tuesday,
                Wednesday,
                Thursday
            )

        fun differenceBetweenPresentAndAlarmDay(presentDay: DayOfWeek, alarmDay: DayOfWeek): Int {
            return when {
                alarmDay < presentDay -> {
                    (7 - presentDay.ordinal)
                }

                alarmDay > presentDay -> {
                    alarmDay.ordinal - presentDay.ordinal
                }
                // presentDay == alarmDay
                else -> 7
            }
        }

        fun convertCalendarDayOfWeekToDayOfWeek(calendarDayOfWeek: Int): DayOfWeek {
            return when (calendarDayOfWeek) {
                2 -> Monday
                3 -> Tuesday
                4 -> Wednesday
                5 -> Thursday
                6 -> Friday
                7 -> Saturday
                1 -> Sunday
                else -> throw IllegalStateException()
            }
        }

    }
}