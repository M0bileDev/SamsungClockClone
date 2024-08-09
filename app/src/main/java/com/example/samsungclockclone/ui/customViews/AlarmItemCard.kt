@file:OptIn(ExperimentalFoundationApi::class)

package com.example.samsungclockclone.ui.customViews

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.scheduler.AlarmId
import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.ext.toDate
import com.example.samsungclockclone.presentation.addAlarm.utils.toNameResourceList
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.HOURS_MINUTES
import com.example.samsungclockclone.ui.utils.SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun AlarmItemCard(
    modifier: Modifier = Modifier,
    alarmItem: AlarmItem,
    onCheckedChange: (AlarmId) -> Unit,
    onLongClick: () -> Unit
) = with(alarmItem) {
    Card(
        modifier = modifier
            .height(90.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), verticalArrangement = Arrangement.Center
            ) {
                if (name.isNotEmpty()) {
                    Text(
                        text = name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(8.dp))

                }
                Text(
                    text = fireTime.toDate(HOURS_MINUTES),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (mode) {
                    AlarmMode.OnlyTime, AlarmMode.CalendarDateAndTime -> {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = fireTime.toDate(SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH)
                        )
                    }

                    AlarmMode.DayOfWeekAndTime -> if (selectedDaysOfWeek.size < 7) {
                        PointerSelectedItems(
                            modifier = Modifier.weight(1f),
                            items = daysOfWeek.toNameResourceList(),
                            selectedItems = selectedDaysOfWeek.toNameResourceList()
                        )
                    } else {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = strings.everyday)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Switch(checked = enable, onCheckedChange = { onCheckedChange(alarmId) })
            }
        }
    }
}

@Preview
@Composable
private fun AlarmItemCardPreview() {
    SamsungClockCloneTheme {
        AlarmItemCard(
            alarmItem = AlarmItem.alarmItemPreview,
            onCheckedChange = {},
            onLongClick = {}
        )
    }
}

@Preview
@Composable
private fun AlarmItemCardPreview2() {
    SamsungClockCloneTheme {
        AlarmItemCard(
            alarmItem = AlarmItem.alarmItemPreview2,
            onCheckedChange = {},
            onLongClick = {}
        )
    }
}

@Preview
@Composable
private fun AlarmItemCardPreview3() {
    SamsungClockCloneTheme {
        AlarmItemCard(
            alarmItem = AlarmItem.alarmItemPreview3,
            onCheckedChange = {},
            onLongClick = {}
        )
    }
}

@Preview
@Composable
private fun AlarmItemCardPreview4() {
    SamsungClockCloneTheme {
        AlarmItemCard(
            alarmItem = AlarmItem.alarmItemPreview4,
            onCheckedChange = {},
            onLongClick = {}
        )
    }
}