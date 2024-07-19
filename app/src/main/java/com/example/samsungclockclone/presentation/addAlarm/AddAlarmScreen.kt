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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.samsungclockclone.data.utils.TimeFormat
import com.example.samsungclockclone.domain.utils.DayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.AddAlarmUiState.AddAlarmUiStateHelper.alarmUiStatePreview
import com.example.samsungclockclone.presentation.addAlarm.utils.toDayOfWeek
import com.example.samsungclockclone.presentation.addAlarm.utils.toNameResourceList
import com.example.samsungclockclone.presentation.addAlarm.utils.toStringResource
import com.example.samsungclockclone.ui.customViews.HorizontalChipGroup
import com.example.samsungclockclone.ui.customViews.SectionSwitch
import com.example.samsungclockclone.ui.customViews.SwipeableClock
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

@Composable
fun AddAlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AddAlarmUiState,
    datePickerState: DatePickerState,
    onHourChanged: (Int) -> Unit,
    onMinuteChanged: (Int) -> Unit,
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
            Row(Modifier.fillMaxWidth()) {
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onCancel
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    onClick = onSave
                ) {
                    Text(text = "Save")
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
                    }
                )
                Text(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    fontSize = 40.sp,
                    text = ":"
                )
                SwipeableClock(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    timeFormat = TimeFormat.Minutes,
                    onValueChanged = { minute ->
                        onMinuteChanged(minute)
                    }
                )
            }
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    AlarmScheduleInfoCalendar(
                        scheduleInfo = addAlarmString.toStringResource(),
                        onDisplayDatePicker = onDisplayDatePicker
                    )
                    HorizontalChipGroup(
                        modifier = Modifier.fillMaxWidth(),
                        // TODO: Think about optimize solution
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
                            Text(text = "Alarm name")
                        },
                        value = alarmName,
                        singleLine = true,
                        onValueChange = onNameChanged,
                        trailingIcon = {
                            if (alarmName.isNotEmpty()) {
                                IconButton(onClick = { onNameChanged("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        "Clear alarm name text input"
                                    )
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionSwitch(
                        header = "Alarm sound",
                        body = soundName,
                        checked = soundEnabled,
                        onCheckedChange = {}
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    SectionSwitch(
                        header = "Vibration",
                        body = vibrationName,
                        checked = vibrationEnabled,
                        onCheckedChange = {}
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    SectionSwitch(
                        header = "Snooze",
                        body = "$snoozeIntervalName, $snoozeRepeatName",
                        checked = soundEnabled,
                        onCheckedChange = {}
                    )
                }
            }
        }
        if (displayPermissionRequire) {
            // TODO: extract strings
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null
                    )
                },
                title = {
                    Text("Permission required")
                },
                text = {
                    Text("You must confirm the \"Alarms & reminders\" special permission.")
                },
                onDismissRequest = onDismissRequest,
                dismissButton = {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "Dismiss")
                    }
                },
                confirmButton = {
                    TextButton(onClick = onRequestSchedulePermission) {
                        Text(text = "Settings")
                    }
                }
            )
        }
        if (displayDatePicker) {
            DatePickerDialog(
                onDismissRequest = onDismissDatePicker,
                dismissButton = {
                    TextButton(onClick = onDismissDatePicker) {
                        Text(text = "Dismiss")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onDateChanged(
                            datePickerState.selectedDateMillis ?: throw IllegalStateException()
                        )
                    }) {
                        Text(text = "OK")
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
                contentDescription = "Select date from calendar"
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
            uiState = alarmUiStatePreview,
            datePickerState = DatePickerState(CalendarLocale.ROOT),
            onHourChanged = {},
            onMinuteChanged = {},
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





