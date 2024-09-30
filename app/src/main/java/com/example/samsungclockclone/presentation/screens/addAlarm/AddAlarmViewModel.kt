package com.example.samsungclockclone.presentation.screens.addAlarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.model.addAlarm.AddAlarmString
import com.example.samsungclockclone.framework.ticker.TimeTicker
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.model.AlarmMode
import com.example.samsungclockclone.domain.model.DayOfWeek
import com.example.samsungclockclone.domain.model.DayOfWeek.DayOfWeekHelper.convertCalendarDayOfWeekToDayOfWeek
import com.example.samsungclockclone.domain.model.DayOfWeek.DayOfWeekHelper.differenceBetweenPresentAndAlarmDay
import com.example.samsungclockclone.presentation.screens.addAlarm.utils.AddAlarmStringType
import com.example.samsungclockclone.presentation.screens.editAlarm.utils.ALARM_ID_KEY
import com.example.samsungclockclone.presentation.utils.SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH
import com.example.samsungclockclone.usecase.GetAlarmByIdUseCase
import com.example.samsungclockclone.usecase.SaveAlarmUseCase
import com.example.samsungclockclone.usecase.UpdateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val timeTicker: TimeTicker
) : ViewModel() {

    sealed interface AddAlarmAction {
        data object ScheduleCompleted : AddAlarmAction
        data object RequestSchedulePermission : AddAlarmAction
        data object NavigateBack : AddAlarmAction
    }

    private val addAlarmActions = Channel<AddAlarmAction>()
    val actions = addAlarmActions.receiveAsFlow()

    private var alarmId: AlarmId = -1L
    private val editAlarm
        get() = alarmId != -1L
    private val dateTimeFormatter =
        DateTimeFormatter.ofPattern(SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH)

    private val alarmHour = MutableStateFlow(0)
    private val alarmMinute = MutableStateFlow(0)
    private val calendarDateMilliseconds = MutableStateFlow(0L)
    private val selectedDaysOfWeek: MutableStateFlow<List<DayOfWeek>> =
        MutableStateFlow(emptyList())
    private val alarmMode = MutableStateFlow(AlarmMode.OnlyTime)
    private val alarmName = MutableStateFlow("")
    private val displayPermissionRequire = MutableStateFlow(false)
    private val displayDatePicker = MutableStateFlow(false)
    private val tickMillis = MutableStateFlow(0L)

    val uiState = combine(
        combine(alarmHour, alarmMinute, ::Pair),
        combine(calendarDateMilliseconds, selectedDaysOfWeek, ::Pair),
        combine(alarmMode, alarmName, ::Pair),
        combine(displayPermissionRequire, displayDatePicker, ::Pair),
        combine(tickMillis, MutableStateFlow(Unit), ::Pair)
    ) { hourAndMinute, calendarAndDays, modeAndName, permissionAndDatePicker, _ ->

        val (hour, minute) = hourAndMinute
        val (calendarDateMilliseconds, selectedDaysOfWeek) = calendarAndDays
        val (alarmMode, alarmName) = modeAndName
        val (displayPermissionRequire, displayDatePicker) = permissionAndDatePicker


        val scheduleInfo = when (alarmMode) {
            AlarmMode.OnlyTime -> {
                checkTodayOrTomorrow(
                    hour,
                    minute,
                    onToday = { actualDateTime ->
                        val date = actualDateTime.format(dateTimeFormatter)
                        AddAlarmString(AddAlarmStringType.TodayX, date)
                    },
                    onTomorrow = { actualDateTime ->
                        val tomorrowDate = actualDateTime.plusDays(1)
                        val date = tomorrowDate.format(dateTimeFormatter)
                        AddAlarmString(AddAlarmStringType.TomorrowX, date)
                    }
                )
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

    init {
        alarmId = handleAlarmId()

        if (editAlarm) {
            overrideAlarm()
        }

        synchronizeWithClockTick()
    }

    private fun synchronizeWithClockTick() {
        viewModelScope.launch {
            timeTicker.onGetTick().collectLatest {
                tickMillis.value = it
            }
        }
    }

    /**
     * Function compares arguments hour and minute with built in LocalDateTime.
     * Function returns generic type if any is passed.
     *
     * onActualDateTime provide default call LocalDateTime.now() which can be override.
     *
     * onToday and onTomorrow have to be provided.
     *
     * 1) If hours (passing through argument and LocalDateTime.now()) are equals, check minutes.
     * 1a) If passed minutes are smaller than actual date call onTomorrow()
     * 1b) If passed minutes are greater than actual date call onToday()
     * 2) If passed hours are smaller than actual date call onTomorrow()
     * 3) If none of above conditions are met call onToday()
     */
    private fun <T> checkTodayOrTomorrow(
        hour: Int,
        minute: Int,
        onActualDateTime: () -> LocalDateTime = { LocalDateTime.now() },
        onToday: (LocalDateTime) -> T,
        onTomorrow: (LocalDateTime) -> T
    ): T {

        val actualDateTime = onActualDateTime()

        return if (hour == actualDateTime.hour) {
            if (minute <= actualDateTime.minute) {
                onTomorrow(actualDateTime)
            } else {
                onToday(actualDateTime)
            }
        } else if (hour <= actualDateTime.hour) {
            onTomorrow(actualDateTime)
        } else {
            onToday(actualDateTime)
        }
    }

    private fun overrideAlarm() {
        viewModelScope.launch {
            getAlarmByIdUseCase(
                alarmId,
                onDataCompleted = { data ->

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = data.alarmMangerEntityList[0].fireTime
                    }

                    alarmHour.value = calendar.get(HOUR_OF_DAY)
                    alarmMinute.value = calendar.get(MINUTE)
                    alarmMode.value = data.alarmEntity.mode


                    when (data.alarmEntity.mode) {
                        AlarmMode.OnlyTime -> {
                            //nothing to update
                        }

                        AlarmMode.DayOfWeekAndTime -> {
                            val daysOfWeek =
                                data.alarmMangerEntityList.map { manager ->
                                    manager.dayOfWeek ?: throw IllegalStateException()
                                }
                            selectedDaysOfWeek.value = daysOfWeek
                        }

                        AlarmMode.CalendarDateAndTime -> {
                            calendarDateMilliseconds.value = data.alarmMangerEntityList[0].fireTime
                        }
                    }

                    alarmName.value = data.alarmEntity.name
                },
                this
            )
        }
    }

    private fun handleAlarmId(): AlarmId {
        return checkNotNull(savedStateHandle[ALARM_ID_KEY])
    }

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
        val alarmMillisecondsList = createAlarmMilliseconds()
        viewModelScope.launch {
            if (editAlarm) {
                updateAlarmUseCase(
                    alarmId,
                    alarmMode.value,
                    alarmMillisecondsList,
                    selectedDaysOfWeek.value,
                    ::onScheduleCompleted,
                    ::onScheduleDenied,
                    this
                )
            } else {
                saveAlarmUseCase(
                    alarmMode.value,
                    alarmName.value,
                    alarmMillisecondsList,
                    selectedDaysOfWeek.value,
                    ::onScheduleCompleted,
                    ::onScheduleDenied,
                    this
                )
            }
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
                checkTodayOrTomorrow(
                    alarmHour.value,
                    alarmMinute.value,
                    onToday = { dateTime ->
                        val alarmMilliseconds = dateTime
                            .withHour(alarmHour.value)
                            .withMinute(alarmMinute.value)
                            .withSecond(0)
                            .withNano(0)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        listOf(alarmMilliseconds)
                    },
                    onTomorrow = { dateTime ->
                        val alarmMilliseconds = dateTime
                            .withHour(alarmHour.value)
                            .withMinute(alarmMinute.value)
                            .withSecond(0)
                            .withNano(0)
                            .plusDays(1)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        listOf(alarmMilliseconds)
                    },
                    onActualDateTime = { actualDateTime }
                )
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
                        set(Calendar.HOUR_OF_DAY, alarmHour.value)
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

    //Value used to update hour when update alarm
    fun onMoveToHour(): Int {
        return alarmHour.value
    }

    //Value used to update minute when update alarm
    fun onMoveToMinute(): Int {
        return alarmMinute.value
    }
}


