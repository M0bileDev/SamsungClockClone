package com.example.samsungclockclone.data.utils

fun convertTimeFormatToString(timeValue: Int, timeFormat: TimeFormat): String {
    val convertedFormat = formatTimeValue(timeValue, timeFormat)
    return if (convertedFormat < 10) {
        "0$convertedFormat"
    } else {
        "$convertedFormat"
    }
}

fun formatTimeValue(
    timeValue: Int,
    timeFormat: TimeFormat
) = when (timeFormat) {
    is TimeFormat.Hours -> convertToHours(timeValue)
    is TimeFormat.Minutes -> convertToMinutes(timeValue)
    is TimeFormat.Seconds -> convertToSeconds(timeValue)
    is TimeFormat.Unconfined -> convertToUnconfined(timeValue, timeFormat.maxValue)
}

private fun convertToHours(value: Int): Int {
    check(value < TimeFormat.Hours.format)
    val convertedValue = value % TimeFormat.Hours.format
    return convertToPositive(convertedValue)
}

private fun convertToMinutes(value: Int): Int {
    check(value < TimeFormat.Minutes.format)
    val convertedValue = value % TimeFormat.Minutes.format
    return convertToPositive(convertedValue)
}

private fun convertToSeconds(value: Int): Int {
    check(value < TimeFormat.Seconds.format)
    val convertedValue = value % TimeFormat.Seconds.format
    return convertToPositive(convertedValue)
}

private fun convertToUnconfined(value: Int, maxValue: Int): Int {
    check(value < maxValue)
    val convertedValue = value % maxValue
    return convertToPositive(convertedValue)
}

private fun convertToPositive(convertedValue: Int) =
    if (convertedValue < 0) (convertedValue * -1) else convertedValue

sealed class TimeFormat(val format: Int = 0) {
    data object Hours : TimeFormat(24)
    data object Minutes : TimeFormat(60)
    data object Seconds : TimeFormat(60)
    data class Unconfined(val maxValue: Int) : TimeFormat(maxValue)
}