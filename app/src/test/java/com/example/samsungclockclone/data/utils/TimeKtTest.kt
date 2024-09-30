package com.example.samsungclockclone.data.utils

import com.google.common.truth.Truth
import org.junit.Assert
import org.junit.Test

class TimeKtTest {

    @Test
    fun `given hour value 0, when time format is HOUR, then value is 01`() {
        //GIVEN
        val hourValue = 0

        //WHEN
        val sut = convertTimeFormatToString(hourValue, TimeFormat.Hours)

        //THEN
        Truth.assertThat(sut).contains(hourValue.toString())
    }

    @Test
    fun `given hour value 23, when time format is HOUR, then value is 23`() {
        //GIVEN
        val hourValue = 23

        //WHEN
        val sut = convertTimeFormatToString(hourValue, TimeFormat.Hours)

        //THEN
        Truth.assertThat(sut).contains(hourValue.toString())
    }

    @Test
    fun `given not existed hour value, when time format is HOUR, then IllegalStateException is thrown`() {
        //GIVEN
        val notExistedHour = 24

        //THEN
        Assert.assertThrows(IllegalStateException::class.java) {

            //WHEN
            convertTimeFormatToString(notExistedHour, TimeFormat.Hours)
        }
    }

    @Test
    fun `given hour value 0, when time format is MINUTES, then value is 01`() {
        //GIVEN
        val minuteValue = 0

        //WHEN
        val sut = convertTimeFormatToString(minuteValue, TimeFormat.Minutes)

        //THEN
        Truth.assertThat(sut).contains(minuteValue.toString())
    }

    @Test
    fun `given hour value 59, when time format is MINUTES, then value is 59`() {
        //GIVEN
        val minuteValue = 59

        //WHEN
        val sut = convertTimeFormatToString(minuteValue, TimeFormat.Minutes)

        //THEN
        Truth.assertThat(sut).contains(minuteValue.toString())
    }

    @Test
    fun `given not existed minute value, when time format is MINUTES, then IllegalStateException is thrown`() {
        //GIVEN
        val notExistedMinute = 60

        //THEN
        Assert.assertThrows(IllegalStateException::class.java) {

            //WHEN
            convertTimeFormatToString(notExistedMinute, TimeFormat.Minutes)
        }
    }

    @Test
    fun `given second value 0, when time format is SECONDS, then value is 01`() {
        //GIVEN
        val secondValue = 0

        //WHEN
        val sut = convertTimeFormatToString(secondValue, TimeFormat.Seconds)

        //THEN
        Truth.assertThat(sut).contains(secondValue.toString())
    }

    @Test
    fun `given second value 59, when time format is SECONDS, then value is 59`() {
        //GIVEN
        val secondValue = 59

        //WHEN
        val sut = convertTimeFormatToString(secondValue, TimeFormat.Seconds)

        //THEN
        Truth.assertThat(sut).contains(secondValue.toString())
    }

    @Test
    fun `given not existed second value, when time format is SECONDS, then IllegalStateException is thrown`() {
        //GIVEN
        val notExistedSecond = 60

        //THEN
        Assert.assertThrows(IllegalStateException::class.java) {

            //WHEN
            convertTimeFormatToString(notExistedSecond, TimeFormat.Minutes)
        }
    }

    @Test
    fun `given time value 0 and max value 100, when time format is UNCONFINED, then value is 01`() {
        //GIVEN
        val timeValue = 0
        val maxValue = 100

        //WHEN
        val sut = convertTimeFormatToString(timeValue, TimeFormat.Unconfined(maxValue))

        //THEN
        Truth.assertThat(sut).contains(timeValue.toString())
    }

    @Test
    fun `given time value 99 and max value 100, when time format is UNCONFINED, then value is 99`() {
        //GIVEN
        val timeValue = 99
        val maxValue = 100

        //WHEN
        val sut = convertTimeFormatToString(timeValue, TimeFormat.Unconfined(maxValue))

        //THEN
        Truth.assertThat(sut).contains(timeValue.toString())
    }

    @Test
    fun `given time value equal to max value, when time format is UNCONFINED, then IllegalStateException is thrown`() {
        //GIVEN
        val timeValue = 100
        val maxValue = 100

        //THEN
        Assert.assertThrows(IllegalStateException::class.java) {

            //WHEN
            convertTimeFormatToString(timeValue, TimeFormat.Unconfined(maxValue))
        }
    }

}