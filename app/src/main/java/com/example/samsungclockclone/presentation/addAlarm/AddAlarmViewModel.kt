package com.example.samsungclockclone.presentation.addAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.model.AlarmEntity
import com.example.samsungclockclone.data.local.model.AlarmManagerEntity
import com.example.samsungclockclone.data.local.scheduler.AlarmScheduler
import com.example.samsungclockclone.presentation.addAlarm.model.AlarmMode
import com.example.samsungclockclone.presentation.addAlarm.model.AlarmRepeat
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.differenceBetweenDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val alarmDao: AlarmDao
) : ViewModel() {

    private val addAlarmUiState = MutableStateFlow(AddAlarmUiState())
    val uiState = addAlarmUiState.asStateFlow()

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

    private fun createAlarmRepeat(): AlarmRepeat {
        return when (alarmMode) {
            AlarmMode.OnlyTime -> AlarmRepeat.EveryDay
            AlarmMode.DayOfWeekAndTime -> AlarmRepeat.EveryWeek
            AlarmMode.CalendarDateAndTime -> AlarmRepeat.EveryDay
        }
    }


    fun onSave() {
        val alarmMillisecondsList = createAlarmMilliseconds()

        //save alarm information inside database
        viewModelScope.launch {
            val alarmName = addAlarmUiState.value.alarmName
            val alarmEntity = AlarmEntity(name = alarmName, enable = true)
            val alarmId = alarmDao.insertAlarm(alarmEntity)

            val alarmRepeat = createAlarmRepeat()
            val alarms = alarmMillisecondsList.map { alarmMilliseconds ->
                val alarmManagerEntity = AlarmManagerEntity(
                    parentId = alarmId,
                    fireTime = alarmMilliseconds,
                    repeat = alarmRepeat.name
                )
                alarmMilliseconds to alarmDao.insertAlarmManager(alarmManagerEntity)
            }

            //save alarms via AlarmManager
            alarms.forEach { alarm ->
                alarmScheduler.schedule(alarm.first, alarm.second)
            }
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


