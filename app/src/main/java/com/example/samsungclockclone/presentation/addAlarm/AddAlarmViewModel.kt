package com.example.samsungclockclone.presentation.addAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.data.local.dao.AlarmDao
import com.example.samsungclockclone.data.local.scheduler.AlarmScheduler
import com.example.samsungclockclone.domain.model.AddAlarmStringType
import com.example.samsungclockclone.domain.model.AddAlarmString
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.model.DayOfWeek
import com.example.samsungclockclone.domain.model.DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek
import com.example.samsungclockclone.domain.model.DayOfWeek.DayOfWeekHelper.differenceBetweenPresentAndAlarmDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
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


    private val alarmHour = MutableStateFlow(0)
    private val alarmMinute = MutableStateFlow(0)
    private val calendarDateMilliseconds = MutableStateFlow(0L)
    private val selectedDaysOfWeek: MutableStateFlow<List<DayOfWeek>> =
        MutableStateFlow(emptyList())
    private val alarmMode = MutableStateFlow(AlarmMode.OnlyTime)
    private val alarmName = MutableStateFlow("")
    private val displayPermissionRequire = MutableStateFlow(false)
    private val displayDatePicker = MutableStateFlow(false)

    val uiState = combine(
        combine(alarmHour, alarmMinute, ::Pair),
        combine(calendarDateMilliseconds, selectedDaysOfWeek, ::Pair),
        combine(alarmMode, alarmName, ::Pair),
        combine(displayPermissionRequire, displayDatePicker, ::Pair)
    ) { hourAndMinute, calendarAndDays, modeAndName, permissionAndDatePicker ->

        val (hour, minute) = hourAndMinute
        val (calendarDateMilliseconds, selectedDaysOfWeek) = calendarAndDays
        val (alarmMode, alarmName) = modeAndName
        val (displayPermissionRequire, displayDatePicker) = permissionAndDatePicker

        val actualDateTime = LocalDateTime.now()
        val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")

        val scheduleInfo = when (alarmMode) {
            AlarmMode.OnlyTime -> {
                if (hour <= actualDateTime.hour && minute <= actualDateTime.minute) {
                    val tomorrowDate = actualDateTime.plusDays(1)
                    val date = tomorrowDate.format(dateTimeFormatter)
                    AddAlarmString(AddAlarmStringType.TomorrowX, date)
                } else {
                    val date = actualDateTime.format(dateTimeFormatter)
                    AddAlarmString(AddAlarmStringType.TodayX, date)
                }
            }

            AlarmMode.DayOfWeekAndTime -> {
                if (selectedDaysOfWeek.size == DayOfWeek.entries.size) {
                    AddAlarmString(AddAlarmStringType.Everyday)
                } else {
                    AddAlarmString(
                        AddAlarmStringType.EveryX,
                        daysOfWeek = selectedDaysOfWeek
                    )
                }
            }

            AlarmMode.CalendarDateAndTime -> {
                val calendarTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(calendarDateMilliseconds),
                    ZoneId.systemDefault()
                )
                val date = calendarTime.format(dateTimeFormatter)
                AddAlarmString(AddAlarmStringType.ValueOnly, date)
            }
        }

        AddAlarmUiState(
            addAlarmString = scheduleInfo,
            selectedDaysOfWeek = selectedDaysOfWeek,
            alarmName = alarmName,
            displayPermissionRequire = displayPermissionRequire,
            displayDatePicker = displayDatePicker
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddAlarmUiState()
    )

//    val addAlarmUiState = combine(
//        combine(alarmHour,
//            alarmMinute,
//            calendarDateMilliseconds,
//            selectedDaysOfWeek,
//            alarmMode),
//        combine(alarmName,
//            displayPermissionRequire,
//            displayDatePicker)
//    ) { alarmHour,
//        alarmMinute,
//        calendarDateMilliseconds,
//        selectedDaysOfWeek,
//        alarmMode,
//        alarmName,
//        displayPermissionRequire,
//        displayDatePicker ->
//
//        val actualDateTime = LocalDateTime.now()
//        val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
//
//        val date = when (alarmMode) {
//            AlarmMode.OnlyTime -> {
//                if (alarmHour <= actualDateTime.hour && alarmMinute <= actualDateTime.minute) {
//                    val tomorrowDate = actualDateTime.plusDays(1)
//                    val date = tomorrowDate.format(dateTimeFormatter)
//                    "Tomorrow-$date"
//                } else {
//                    val date = actualDateTime.format(dateTimeFormatter)
//                    "Today-$date"
//                }
//            }
//
//            AlarmMode.DayOfWeekAndTime -> {
//                if (selectedDaysOfWeek.size == DayOfWeek.entries.size) {
//                    "Every day"
//                } else {
//                    selectedDaysOfWeek.map { it.name }.reduce { acc, dayOfWeek -> acc + dayOfWeek }
//                        .dropLast(1)
//                }
//
//            }
//
//            AlarmMode.CalendarDateAndTime -> ""
//        }
//        println("LOGS $date")
//        date
//    }

//    init {
//        viewModelScope.launch {
//            info.collectLatest { info ->
//                addAlarmUiState.update { previousState ->
//                    previousState.copy(scheduleInfo = info)
//                }
//            }
//        }
//    }

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
        val copySelectedDaysOfWeek = selectedDaysOfWeek.value.toMutableList()

        if (copySelectedDaysOfWeek.contains(dayOfWeek)) {
            copySelectedDaysOfWeek.remove(dayOfWeek)
        } else {
            copySelectedDaysOfWeek.add(dayOfWeek)
        }
        //Sort days
        copySelectedDaysOfWeek.sortBy { it.ordinal }

        selectedDaysOfWeek.value = copySelectedDaysOfWeek

        alarmMode.value = if (copySelectedDaysOfWeek.isNotEmpty()) {
            AlarmMode.DayOfWeekAndTime
        } else {
            AlarmMode.OnlyTime
        }

    }

    fun nameChanged(name: String) {
        alarmName.value = name
    }

//    private fun createAlarmRepeat(): AlarmRepeat {
//        return when (alarmMode.value) {
//            AlarmMode.OnlyTime -> AlarmRepeat.EveryDay
//            AlarmMode.DayOfWeekAndTime -> AlarmRepeat.EveryWeek
//            AlarmMode.CalendarDateAndTime -> AlarmRepeat.EveryDay
//        }
//    }


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
        displayPermissionRequire.value = true
    }

    fun onRequestSchedulePermission() {
        displayPermissionRequire.value = false
        viewModelScope.launch {
            addAlarmActions.send(AddAlarmAction.RequestSchedulePermission)
        }
    }

    fun dismissSchedulePermission() {
        displayPermissionRequire.value = false
    }

    fun onDisplayDatePicker() {
        displayDatePicker.value = true
    }

    fun onDismissDatePicker() {
        displayDatePicker.value = false

    }

    fun onDateChanged(selectedDateMillis: Long) {
        displayDatePicker.value = false
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
                val presentDay = convertCalendarDayOfWeekToDayOfWeek(calendarDayOfWeek)
                val alarmMillisecondsList = selectedDaysOfWeek.value.map { alarmDay ->
                    val daysDifference = differenceBetweenPresentAndAlarmDay(
                        presentDay = presentDay,
                        alarmDay = alarmDay
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


