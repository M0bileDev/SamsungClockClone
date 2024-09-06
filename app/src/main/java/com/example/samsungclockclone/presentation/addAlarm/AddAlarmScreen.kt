@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation.addAlarm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.samsungclockclone.R
import com.example.samsungclockclone.data.utils.TimeFormat
import com.example.samsungclockclone.domain.utils.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.AddAlarmUiState.AddAlarmUiStateHelper.addAlarmUiStatePreview
import com.example.samsungclockclone.presentation.addAlarm.utils.toDayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.utils.toNameResourceList
import com.example.samsungclockclone.presentation.addAlarm.utils.toStringResource
import com.example.samsungclockclone.ui.customViews.HorizontalChipGroup
import com.example.samsungclockclone.ui.customViews.SectionSwitch
import com.example.samsungclockclone.ui.customViews.SwipeableClock
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun AddAlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AddAlarmUiState,
    datePickerState: DatePickerState,
    onHourChanged: (Int) -> Unit,
    onMoveToHour: () -> Int,
    onMinuteChanged: (Int) -> Unit,
    onMoveToMinute: () -> Int,
    onDateChanged: (Long) -> Unit,
    onDayOfWeekChanged: (DayOfWeek) -> Unit,
    onNameChanged: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onDismissRequest: () -> Unit,
    onRequestSchedulePermission: () -> Unit,
    onDisplayDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
) = with(uiState) {
    Scaffold(modifier = modifier,
        bottomBar = {
            val bottomTextStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp)
            Row(Modifier.fillMaxWidth()) {
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onCancel
                ) {
                    Text(text = stringResource(R.string.cancel), style = bottomTextStyle)
                }
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onSave
                ) {
                    Text(text = stringResource(R.string.save), style = bottomTextStyle)
                }
            }
        }) {
        Column(modifier = Modifier.padding(it)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                SwipeableClock(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    timeFormat = TimeFormat.Hours,
                    onValueChanged = { hour ->
                        onHourChanged(hour)
                    },
                    onMoveToValue = onMoveToHour
                )
                Text(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    fontSize = 40.sp,
                    text = stringResource(R.string.separator)
                )
                SwipeableClock(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    timeFormat = TimeFormat.Minutes,
                    onValueChanged = { minute ->
                        onMinuteChanged(minute)
                    },
                    onMoveToValue = onMoveToMinute
                )
            }
            Card {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    AlarmScheduleInfoCalendar(
                        scheduleInfo = addAlarmString.toStringResource(),
                        onDisplayDatePicker = onDisplayDatePicker
                    )
                    HorizontalChipGroup(
                        modifier = Modifier.fillMaxWidth(),
                        items = daysOfWeek.toNameResourceList(),
                        selectedItems = selectedDaysOfWeek.toNameResourceList(),
                        onSelected = { resource ->
                            val dayOfWeek = resource.nameResourceValue.toDayOfWeek()
                            onDayOfWeekChanged(dayOfWeek)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(R.string.alarm_name))
                        },
                        value = alarmName,
                        singleLine = true,
                        onValueChange = onNameChanged,
                        trailingIcon = {
                            if (alarmName.isNotEmpty()) {
                                IconButton(onClick = { onNameChanged("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        stringResource(R.string.content_description_clear_alarm_name_text_input)
                                    )
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionSwitch(
                        header = stringResource(R.string.alarm_sound),
                        body = soundName,
                        checked = soundEnabled,
                        onCheckedChange = {}
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    SectionSwitch(
                        header = stringResource(R.string.vibration),
                        body = vibrationName,
                        checked = vibrationEnabled,
                        onCheckedChange = {}
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    SectionSwitch(
                        header = stringResource(R.string.snooze),
                        body = stringResource(
                            id = strings.x_separator_y_values,
                            snoozeIntervalName,
                            ",",
                            snoozeRepeatName
                        ),
                        checked = soundEnabled,
                        onCheckedChange = {}
                    )
                }
            }
        }
        if (displayPermissionRequire) {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null
                    )
                },
                title = {
                    Text(stringResource(R.string.permission_required))
                },
                text = {
                    Text(stringResource(R.string.you_must_confirm_the_alarms_reminders_special_permission))
                },
                onDismissRequest = onDismissRequest,
                dismissButton = {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                },
                confirmButton = {
                    TextButton(onClick = onRequestSchedulePermission) {
                        Text(text = stringResource(R.string.settings))
                    }
                }
            )
        }
        if (displayDatePicker) {
            DatePickerDialog(
                onDismissRequest = onDismissDatePicker,
                dismissButton = {
                    TextButton(onClick = onDismissDatePicker) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onDateChanged(
                            datePickerState.selectedDateMillis ?: throw IllegalStateException()
                        )
                    }) {
                        Text(text = stringResource(R.string.ok))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

    }
}

@Composable
private fun AlarmScheduleInfoCalendar(
    scheduleInfo: String,
    onDisplayDatePicker: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.Start),
            text = scheduleInfo
        )
        IconButton(
            modifier = Modifier
                .wrapContentWidth(Alignment.End),
            onClick = onDisplayDatePicker
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.content_description_select_date_from_calendar)
            )
        }
    }
}

@Preview
@Composable
private fun AddAlarmPreview() {
    SamsungClockCloneTheme {
        AddAlarmScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = addAlarmUiStatePreview,
            datePickerState = DatePickerState(CalendarLocale.ROOT),
            onHourChanged = {},
            onMoveToHour = { 0 },
            onMinuteChanged = {},
            onMoveToMinute = { 0 },
            onDateChanged = {},
            onDayOfWeekChanged = {},
            onNameChanged = {},
            onCancel = {},
            onSave = {},
            onDismissRequest = {},
            onRequestSchedulePermission = {},
            onDisplayDatePicker = {},
            onDismissDatePicker = {},
        )
    }
}





