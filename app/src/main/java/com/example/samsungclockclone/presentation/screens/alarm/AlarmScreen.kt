@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation.screens.alarm

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.R
import com.example.samsungclockclone.domain.model.AlarmOrder
import com.example.samsungclockclone.domain.model.alarm.AlarmTitleString
import com.example.samsungclockclone.domain.model.alarm.DifferenceType
import com.example.samsungclockclone.domain.utils.AlarmId
import com.example.samsungclockclone.ext.toDate
import com.example.samsungclockclone.ext.toStringRes
import com.example.samsungclockclone.presentation.screens.alarm.utils.AddAlarmMode
import com.example.samsungclockclone.presentation.screens.alarm.utils.EditAlarmMode
import com.example.samsungclockclone.ui.customViews.AlarmItemCard
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH_HOUR_MINUTE
import com.example.samsungclockclone.ui.utils.plurals
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AlarmUiState,
    onAdd: (AddAlarmMode) -> Unit,
    onEdit: (EditAlarmMode) -> Unit,
    onSort: (AlarmOrder) -> Unit,
    onSettings: () -> Unit,
    onAlarmEnableSwitch: (AlarmId) -> Unit,
    onDismissRequest: () -> Unit,
    onRequestSchedulePermission: () -> Unit,
) = with(uiState) {

    val topAppBarState = rememberTopAppBarState()
    val topAppBarCollapsed by remember(topAppBarState) {
        derivedStateOf {
            topAppBarState.collapsedFraction > 0.5f
        }
    }
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    val resources = LocalContext.current.resources
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    var sortMenuExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    AlarmTitle(topAppBarCollapsed, resources, alarmTitleString)
                },
                actions = {
                    IconButton(
                        onClick = {
                            onAdd(AddAlarmMode.AddAlarmToolbarAction)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.content_description_add_alalrm)
                        )
                    }
                    IconButton(
                        onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_description_manage_drop_down_menu)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }) {
                        if (editAvailable) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.edit)
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEdit(EditAlarmMode.EditAlarmToolbarAction)
                                }
                            )
                        }
                        if (sortAvailable) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.sort)
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    sortMenuExpanded = true
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.settings)
                                )
                            },
                            onClick = onSettings
                        )
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }) {
                        AlarmOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = resources.getString(order.toStringRes())
                                    )
                                },
                                onClick = {
                                    sortMenuExpanded = false
                                    onSort(order)
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        }
    ) {
        if (alarmItems.isEmpty()) {
            NoAlarms(scrollProvider = {
                (-200 * topAppBarState.collapsedFraction).toInt()
            })
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alarmItems) { item ->
                    AlarmItemCard(
                        alarmItem = item,
                        onCheckedChange = onAlarmEnableSwitch,
                        onClick = { onAdd(AddAlarmMode.AddAlarmItemAction(item.alarmId)) },
                        onLongClick = { onEdit(EditAlarmMode.EditAlarmItemAction(item.alarmId)) }
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
    }
}

@Composable
private fun NoAlarms(scrollProvider: () -> Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Text(modifier = Modifier.offset {
            IntOffset(
                0,
                scrollProvider()
            )
        }, text = stringResource(R.string.no_alarms))
    }
}

@Composable
private fun AlarmTitle(
    topAppBarCollapsed: Boolean,
    resources: Resources,
    alarmTitleString: AlarmTitleString
) {
    if (topAppBarCollapsed) {
        Text(
            text = resources.getString(strings.alarm)
        )
    } else {
        when (alarmTitleString) {
            AlarmTitleString.AlarmsOff -> {
                Text(
                    text = resources.getString(strings.alarms_off)
                )
            }

            is AlarmTitleString.NearestAlarm -> {
                val differenceString =
                    when (alarmTitleString.alarmDifference.differenceType) {
                        DifferenceType.DAYS -> pluralStringResource(
                            plurals.x_days,
                            alarmTitleString.alarmDifference.daysDifference,
                            alarmTitleString.alarmDifference.daysDifference
                        )

                        DifferenceType.HOURS_MINUTES -> {
                            val hoursString = pluralStringResource(
                                plurals.x_hours,
                                alarmTitleString.alarmDifference.hoursDifference,
                                alarmTitleString.alarmDifference.hoursDifference
                            )
                            val minutesString = pluralStringResource(
                                plurals.x_minutes,
                                alarmTitleString.alarmDifference.minutesDifference,
                                alarmTitleString.alarmDifference.minutesDifference
                            )
                            stringResource(id = strings.x_y_values, hoursString, minutesString)
                        }

                        DifferenceType.MINUTES -> pluralStringResource(
                            plurals.x_minutes,
                            alarmTitleString.alarmDifference.minutesDifference,
                            alarmTitleString.alarmDifference.minutesDifference
                        )
                    }
                Column {
                    Text(
                        text = resources.getString(
                            strings.alarm_in_x,
                            differenceString
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = alarmTitleString.alarmMillis.toDate(
                            SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH_HOUR_MINUTE
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
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
            onAdd = {},
            onEdit = {},
            onSort = {},
            onSettings = {},
            onAlarmEnableSwitch = {},
            onDismissRequest = {},
            onRequestSchedulePermission = {}
        )
    }
}

