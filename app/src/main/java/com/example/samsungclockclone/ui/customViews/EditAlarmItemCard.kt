package com.example.samsungclockclone.ui.customViews

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.domain.model.alarm.EditAlarmItem
import com.example.samsungclockclone.domain.utils.AlarmId
import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.ext.toDate
import com.example.samsungclockclone.presentation.addAlarm.utils.toNameResourceList
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.HOURS_MINUTES
import com.example.samsungclockclone.ui.utils.SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH
import com.example.samsungclockclone.ui.utils.drawables
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun EditAlarmItemCard(
    modifier: Modifier = Modifier,
    editAlarmItem: EditAlarmItem,
    dragged: Boolean = false,
    onSelectionChanged: (AlarmId) -> Unit,
    onDragIconPress: (Boolean) -> Unit
) = with(editAlarmItem) {
    Card(
        modifier = modifier.height(90.dp),
        border = if (dragged) BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val press = event.type != PointerEventType.Release
                                onDragIconPress(press)
                            }
                        }
                    },
                painter = painterResource(id = drawables.ic_drag),
                contentDescription = stringResource(
                    id = strings.content_desc_change_alarm_id_x_position,
                    alarmItem.alarmId
                )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), verticalArrangement = Arrangement.Center
            ) {
                if (alarmItem.name.isNotEmpty()) {
                    Text(
                        text = alarmItem.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(8.dp))

                }
                Text(
                    text = alarmItem.fireTime.toDate(HOURS_MINUTES),
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
                when (alarmItem.mode) {
                    AlarmMode.OnlyTime, AlarmMode.CalendarDateAndTime -> {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = alarmItem.fireTime.toDate(
                                SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH
                            )
                        )
                    }

                    AlarmMode.DayOfWeekAndTime -> if (alarmItem.selectedDaysOfWeek.size < 7) {
                        PointerSelectedItems(
                            modifier = Modifier.weight(1f),
                            items = alarmItem.daysOfWeek.toNameResourceList(),
                            selectedItems = alarmItem.selectedDaysOfWeek.toNameResourceList()
                        )
                    } else {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = strings.everyday)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                RadioButton(
                    selected = selected,
                    onClick = { onSelectionChanged(alarmItem.alarmId) })
            }
        }
    }
}

@Preview
@Composable
private fun EditAlarmItemCardPreview() {
    SamsungClockCloneTheme {
        EditAlarmItemCard(
            editAlarmItem = EditAlarmItem.editAlarmItemPreview,
            dragged = true,
            onSelectionChanged = {},
            onDragIconPress = {}
        )
    }
}

@Preview
@Composable
private fun EditAlarmItemCardPreview2() {
    SamsungClockCloneTheme {
        EditAlarmItemCard(
            editAlarmItem = EditAlarmItem.editAlarmItemPreview2,
            dragged = true,
            onSelectionChanged = {},
            onDragIconPress = {}
        )
    }
}

@Preview
@Composable
private fun EditAlarmItemCardPreview3() {
    SamsungClockCloneTheme {
        EditAlarmItemCard(
            editAlarmItem = EditAlarmItem.editAlarmItemPreview3,
            onSelectionChanged = {},
            onDragIconPress = {}
        )
    }
}