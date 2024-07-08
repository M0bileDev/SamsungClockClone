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
import androidx.compose.material3.Card
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
import com.example.samsungclockclone.presentation.addAlarm.AddAlarmUiState.AddAlarmUiStateHelper.alarmUiStatePreview
import com.example.samsungclockclone.presentation.addAlarm.model.DayOfWeek
import com.example.samsungclockclone.ui.customViews.HorizontalChipGroup
import com.example.samsungclockclone.ui.customViews.SectionSwitch
import com.example.samsungclockclone.ui.customViews.SwipeableClock
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme


@Composable
fun AddAlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AddAlarmUiState,
    onSelectedFromCalendar: (Int) -> Unit,
    onSelectedDaysOfWeek: (DayOfWeek) -> Unit,
    onChangedAlarmName: (String) -> Unit,
    onChangedAlarmEnabled: (Boolean) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
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
                    timeFormat = TimeFormat.Hours
                ) {

                }
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
                    timeFormat = TimeFormat.Minutes
                ) {

                }
            }
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    AlarmScheduleInfoCalendar(
                        scheduleInfo = scheduleInfo,
                        onSelectedFromCalendar = onSelectedFromCalendar
                    )
                    HorizontalChipGroup(
                        modifier = Modifier.fillMaxWidth(),
                        items = daysOfWeek,
                        selectedItems = selectedDaysOfWeek,
                        onSelected = onSelectedDaysOfWeek
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = "Alarm name")
                        },
                        value = alarmName,
                        singleLine = true,
                        onValueChange = onChangedAlarmName,
                        trailingIcon = {
                            if (alarmName.isNotEmpty()) {
                                IconButton(onClick = { onChangedAlarmName("") }) {
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
                        onCheckedChange = onChangedAlarmEnabled
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    SectionSwitch(
                        header = "Vibration",
                        body = vibrationName,
                        checked = vibrationEnabled,
                        onCheckedChange = onChangedAlarmEnabled
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    SectionSwitch(
                        header = "Snooze",
                        body = "$snoozeIntervalName, $snoozeRepeatName",
                        checked = soundEnabled,
                        onCheckedChange = onChangedAlarmEnabled
                    )
                }
            }
        }

    }
}

@Composable
private fun AlarmScheduleInfoCalendar(
    scheduleInfo: String,
    onSelectedFromCalendar: (Int) -> Unit
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
                .weight(1f)
                .wrapContentWidth(Alignment.End),
            onClick = {
                // TODO: Add choose from calendar
                onSelectedFromCalendar(0)
            }) {
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
            onSelectedFromCalendar = {},
            onSelectedDaysOfWeek = {},
            onChangedAlarmName = {},
            onChangedAlarmEnabled = {},
            onCancel = {},
            onSave = {}
        )
    }
}





