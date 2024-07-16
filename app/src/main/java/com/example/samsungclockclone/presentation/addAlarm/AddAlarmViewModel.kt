package com.example.samsungclockclone.presentation.addAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.scheduler.AlarmScheduler
import com.example.samsungclockclone.presentation.addAlarm.model.AlarmMode
import com.example.samsungclockclone.presentation.addAlarm.model.AlarmRepeat
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek.DayOfWeekHelper.differenceBetweenDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val alarmDao: AlarmDao
) : ViewModel() {

    sealed interface AddAlarmAction {
        data object ScheduleCompleted : AddAlarmAction
        data object RequestSchedulePermission : AddAlarmAction
    }

    private val addAlarmActions = Channel<AddAlarmAction>()
    val actions = addAlarmActions.receiveAsFlow()

    private val addAlarmUiState = MutableStateFlow(AddAlarmUiState())
    val uiState = addAlarmUiState.asStateFlow()

    // TODO: Consider chnging primitive types to MutableState 
    private var alarmHour = MutableStateFlow(0)
    private var alarmMinute = MutableStateFlow(0)
    private var calendarDateMilliseconds = MutableStateFlow(0L)
    private var alarmMode = MutableStateFlow(AlarmMode.OnlyTime)

    val info = combine(
        alarmHour,
        alarmMinute,
        calendarDateMilliseconds,
        alarmMode
    ) { alarmHour, alarmMinute, calendarDateMilliseconds, alarmMode ->

        val actualDateTime = LocalDateTime.now()
        val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")

        val date = when (alarmMode) {
            AlarmMode.OnlyTime -> {
                if (alarmHour <= actualDateTime.hour && alarmMinute <= actualDateTime.minute){
                    val tomorrowDate = actualDateTime.plusDays(1)
                    val date = tomorrowDate.format(dateTimeFormatter)
                    "Tomorrow-$date"
                } else {
                    val date = actualDateTime.format(dateTimeFormatter)
                    "Today-$date"
                }
            }

            AlarmMode.DayOfWeekAndTime -> ""
            AlarmMode.CalendarDateAndTime -> ""
        }
        println("LOGS $date")
        date
    }

    init {
        viewModelScope.launch {
            info.collectLatest { info ->
                addAlarmUiState.update { previousState ->
                    previousState.copy(scheduleInfo = info)
                }
            }
        }
    }

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
        alarmHour.value = hour
    }

    fun minuteChanged(minute: Int) {
        alarmMinute.value = minute
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

        alarmMode.value = if (selectedDaysOfWeek.isNotEmpty()) {
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
        return when (alarmMode.value) {
            AlarmMode.OnlyTime -> AlarmRepeat.EveryDay
            AlarmMode.DayOfWeekAndTime -> AlarmRepeat.EveryWeek
            AlarmMode.CalendarDateAndTime -> AlarmRepeat.EveryDay
        }
    }


    fun onSave() {

        // TODO: add security to check if alarm time is not past time
        val alarmMillisecondsList = createAlarmMilliseconds()

        //save alarm information inside database
        viewModelScope.launch {
//            val alarmName = addAlarmUiState.value.alarmName
//            val alarmEntity = AlarmEntity(name = alarmName, enable = true)
//            val alarmId = alarmDao.insertAlarm(alarmEntity)
//
//            val alarmRepeat = createAlarmRepeat()
//            val alarms: List<Pair<AlarmId, AlarmMilliseconds>> =
//                alarmMillisecondsList.map { alarmMilliseconds ->
//                    val alarmManagerEntity = AlarmManagerEntity(
//                        parentId = alarmId,
//                        fireTime = alarmMilliseconds,
//                        repeat = alarmRepeat.name
//                    )
//                    alarmDao.insertAlarmManager(alarmManagerEntity) to alarmMilliseconds
//                }

            //schedule alarms via alarm manager
            alarmScheduler.schedule(
                emptyList(),
                onScheduleCompleted = ::onScheduleCompleted,
                onScheduleDenied = ::onScheduleDenied
            )
        }

    }

    private fun onScheduleCompleted() {
        viewModelScope.launch {
            addAlarmActions.send(AddAlarmAction.ScheduleCompleted)
        }
    }

    private fun onScheduleDenied() {
        addAlarmUiState.update { previousState ->
            previousState.copy(displayPermissionRequire = true)
        }
    }

    fun onRequestSchedulePermission() {
        addAlarmUiState.update { previousState ->
            previousState.copy(displayPermissionRequire = false)
        }
        viewModelScope.launch {
            addAlarmActions.send(AddAlarmAction.RequestSchedulePermission)
        }
    }

    fun dismissSchedulePermission() {
        addAlarmUiState.update { previousState ->
            previousState.copy(displayPermissionRequire = false)
        }
    }

    fun onDisplayDatePicker() {
        addAlarmUiState.update { previousState ->
            previousState.copy(displayDatePicker = true)
        }
    }

    fun onDismissDatePicker() {
        addAlarmUiState.update { previousState ->
            previousState.copy(displayDatePicker = false)
        }
    }

    fun onDateChanged(selectedDateMillis: Long) {
        addAlarmUiState.update { previousState ->
            previousState.copy(displayDatePicker = false)
        }
        alarmMode.value = AlarmMode.CalendarDateAndTime
        calendarDateMilliseconds.value = selectedDateMillis
    }


    private fun createAlarmMilliseconds(): List<Long> {

        val actualDateTime = LocalDateTime.now()
        val calendar = Calendar.getInstance()

        return when (alarmMode.value) {
            AlarmMode.OnlyTime -> {
                // actual time: 12:00, 11:59 -> next day,  12:00 -> next day, 00:00 -> next day, 12:01 -> same day,  23:59 -> same day
                if (alarmHour.value <= actualDateTime.hour && alarmMinute.value <= actualDateTime.minute) {
                    //alarm next day
                    val alarmMilliseconds = actualDateTime
                        .withHour(alarmHour.value)
                        .withMinute(alarmMinute.value)
                        .plusDays(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    listOf(alarmMilliseconds)
                } else {
                    //alarm same day
                    val alarmMilliseconds = actualDateTime
                        .withHour(alarmHour.value)
                        .withMinute(alarmMinute.value)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    listOf(alarmMilliseconds)
                }
            }

            AlarmMode.DayOfWeekAndTime -> {
                val tmpCalendar = calendar.apply {
                    timeInMillis = System.currentTimeMillis()
                }
                val calendarDayOfWeek = tmpCalendar.get(Calendar.DAY_OF_WEEK)
                val dayOfWeek = convertCalendarDayOfWeekToDayOfWeek(calendarDayOfWeek)
                val alarmMillisecondsList = uiState.value.selectedDaysOfWeek.map { alarmDay ->
                    val daysDifference = differenceBetweenDays(
                        startDay = dayOfWeek,
                        endDay = alarmDay
                    ).toLong()

                    actualDateTime
                        .withHour(alarmHour.value)
                        .withMinute(alarmMinute.value)
                        .plusDays(daysDifference)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }

                alarmMillisecondsList
            }

            AlarmMode.CalendarDateAndTime -> {
                val tmpCalendar = calendar
                    .apply {
                        timeInMillis = calendarDateMilliseconds.value
                        set(Calendar.HOUR, alarmHour.value)
                        set(Calendar.MINUTE, alarmMinute.value)
                    }

                val alarmMilliseconds = tmpCalendar
                    .toInstant()
                    .toEpochMilli()

                listOf(alarmMilliseconds)
            }
        }
    }
}


