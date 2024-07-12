package com.example.samsungclockclone.presentation.addAlarm

import android.app.AlarmManager
import androidx.lifecycle.ViewModel
import com.example.samsungclockclone.presentation.addAlarm.model.AlarmMode
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.differenceBetweenDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val alarmManager: AlarmManager
) : ViewModel() {

    private val addAlarmUiState = MutableStateFlow(AddAlarmUiState())
    val uiState = addAlarmUiState.asStateFlow()

    private var actualDateTime = LocalDateTime.now()
    private var alarmMilliseconds = 0L
    private var alarmHour = 0
    private var alarmMinute = 0
    private var alarmMode = AlarmMode.OnlyTime

//    init {
//        calculateDefaultNextDayAlarm()
//    }
//
//    private fun calculateDefaultNextDayAlarm() {
//        val actualHour = actualDateTime.hour
//        val actualMinute = actualDateTime.minute
//
//        val minutesLeftToNextDay =
//            ((((23 + alarmHour) - actualHour) * 60) + ((60 + alarmMinute) - actualMinute)).toLong()
//        val nextDayTime = actualDateTime.plusMinutes(minutesLeftToNextDay)
//        alarmMilliseconds = nextDayTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
//    }

    fun hourChanged(hour: Int) {
        alarmHour = hour
    }

    fun minuteChanged(minute: Int) {
        alarmMinute = minute
    }

    fun dayOfWeekChanged(dayOfWeek: DayOfWeek) {
        val selectedDaysOfWeek = uiState.value.selectedDaysOfWeek.toMutableList()

        if (selectedDaysOfWeek.contains(dayOfWeek)) {
            selectedDaysOfWeek.remove(dayOfWeek)
        } else {
            selectedDaysOfWeek.add(dayOfWeek)
        }

        addAlarmUiState.update { previousState ->
            previousState.copy(
                selectedDaysOfWeek = selectedDaysOfWeek
            )
        }

        alarmMode = if (selectedDaysOfWeek.isNotEmpty()) {
            AlarmMode.DayOfWeekAndTime
        } else {
            AlarmMode.OnlyTime
        }
    }

    fun nameChanged(name: String) {
        addAlarmUiState.update { previousState ->
            previousState.copy(
                alarmName = name
            )
        }
    }

    fun onSave() {
        val alarmMillisecondsList = createAlarmMilliseconds()



        alarmMillisecondsList.forEach { alarmMilliseconds ->
            //create alarms with alarm manager
            //save alarm information inside database
        }

    }

    private fun createAlarmMilliseconds(): List<Long> {

        val actualDateTime = LocalDateTime.now()

        return when (alarmMode) {
            AlarmMode.OnlyTime -> {
                // actual time: 12:00, 11:59 -> next day,  12:00 -> next day, 00:00 -> next day, 12:01 -> same day,  23:59 -> same day
                if (alarmHour <= actualDateTime.hour || alarmHour == 0) {
                    //alarm next day
                    val alarmMilliseconds = actualDateTime
                        .withHour(alarmHour)
                        .withMinute(alarmMinute)
                        .plusDays(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    listOf(alarmMilliseconds)
                } else {
                    //alarm same day
                    val alarmMilliseconds = actualDateTime
                        .withHour(alarmHour)
                        .withMinute(alarmMinute)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    listOf(alarmMilliseconds)
                }
            }

            AlarmMode.DayOfWeekAndTime -> {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                }
                val calendarDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dayOfWeek = convertCalendarDayOfWeekToDayOfWeek(calendarDayOfWeek)
                val alarmMillisecondsList = uiState.value.selectedDaysOfWeek.map { alarmDay ->
                    val daysDifference = differenceBetweenDays(
                        startDay = dayOfWeek,
                        endDay = alarmDay
                    ).toLong()

                    actualDateTime
                        .withHour(alarmHour)
                        .withMinute(alarmMinute)
                        .plusDays(daysDifference)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }

                alarmMillisecondsList
            }

            AlarmMode.CalendarDateAndTime -> {
                emptyList()
            }
        }
    }


}


