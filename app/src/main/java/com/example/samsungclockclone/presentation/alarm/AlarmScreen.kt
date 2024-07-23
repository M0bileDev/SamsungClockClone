@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.data.local.scheduler.AlarmId
import com.example.samsungclockclone.domain.model.alarm.AlarmItem
import com.example.samsungclockclone.domain.model.alarm.AlarmItem.Companion.alarmItemPreview
import com.example.samsungclockclone.domain.utils.AlarmMode
import com.example.samsungclockclone.ext.toDate
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.HOURS_MINUTES
import com.example.samsungclockclone.ui.utils.SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AlarmUiState,
    onAddAlarm: () -> Unit,
    onEdit: () -> Unit,
    onSort: () -> Unit,
    onSettings: () -> Unit
) = with(uiState) {

    val resources = LocalContext.current.resources
    var expanded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = resources.getString(strings.alarm)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onAddAlarm
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add alalrm"
                        )
                    }
                    IconButton(
                        onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Manage drop down menu"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        if (editAvailable) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Edit"
                                    )
                                },
                                onClick = onEdit
                            )
                        }
                        if (sortAvailable) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Sort"
                                    )
                                },
                                onClick = onSort
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Settings"
                                )
                            },
                            onClick = onSettings
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(alarmItems) {

            }
        }
    }
}

@Composable
fun AlarmItemCard(
    modifier: Modifier = Modifier,
    alarmItem: AlarmItem,
    onChanged: (AlarmId) -> Unit
) = with(alarmItem) {
    Card(modifier.height(IntrinsicSize.Min)) {
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
                        Text(text = fireTime.toDate(SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH))
                    }

                    AlarmMode.DayOfWeekAndTime -> // TODO: implement list of days
                }
                Spacer(modifier = Modifier.size(4.dp))
                Switch(checked = enable, onCheckedChange = { alarmId })
            }
        }
    }
}


@Preview
@Composable
private fun AlarmScreenPreview() {
    SamsungClockCloneTheme {
        AlarmScreen(
            uiState = AlarmUiState.alarmUiStatePreview,
            onAddAlarm = {},
            onEdit = {},
            onSort = {},
            onSettings = {}
        )
    }
}

@Preview
@Composable
private fun AlarmItemCardPreview() {
    SamsungClockCloneTheme {
        AlarmItemCard(
            alarmItem = alarmItemPreview,
            onChanged = {}
        )
    }
}