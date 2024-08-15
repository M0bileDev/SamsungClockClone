package com.example.samsungclockclone.presentation.addAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.model.addAlarm.AddAlarmString
import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.domain.utils.DayOfWeek
import com.example.samsungclockclone.domain.utils.DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek
import com.example.samsungclockclone.domain.utils.DayOfWeek.DayOfWeekHelper.differenceBetweenPresentAndAlarmDay
import com.example.samsungclockclone.presentation.addAlarm.utils.AddAlarmStringType
import com.example.samsungclockclone.ui.utils.SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH
import com.example.samsungclockclone.usecase.SaveAlarmUseCase
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
    private val saveAlarmUseCase: SaveAlarmUseCase
) : ViewModel() {

    sealed interface AddAlarmAction {
        data object ScheduleCompleted : AddAlarmAction
        data object RequestSchedulePermission : AddAlarmAction
        data object NavigateBack : AddAlarmAction
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
        val dateTimeFormatter =
            DateTimeFormatter.ofPattern(SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH)

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

    fun onSave() {
        // TODO: add security to check if alarm time is not past time
        val alarmMillisecondsList = createAlarmMilliseconds()
        viewModelScope.launch {
            saveAlarmUseCase(
                alarmMode.value,
                alarmName.value,
                alarmMillisecondsList,
                this
            )
            addAlarmActions.send(AddAlarmAction.NavigateBack)
        }
    }

    private fun onScheduleCompleted() = viewModelScope.launch {
        addAlarmActions.send(AddAlarmAction.ScheduleCompleted)
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
        selectedDaysOfWeek.value = emptyList()
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
                        .withSecond(0)
                        .withNano(0)
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
                        .withSecond(0)
                        .withNano(0)
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
                        .withSecond(0)
                        .withNano(0)
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
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                val alarmMilliseconds = tmpCalendar
                    .toInstant()
                    .toEpochMilli()

                listOf(alarmMilliseconds)
            }
        }
    }
}


