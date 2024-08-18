@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.domain.model.AlarmOrder
import com.example.samsungclockclone.domain.utils.AlarmId
import com.example.samsungclockclone.ext.toStringRes
import com.example.samsungclockclone.presentation.alarm.utils.AddAlarmMode
import com.example.samsungclockclone.presentation.alarm.utils.EditAlarmMode
import com.example.samsungclockclone.ui.customViews.AlarmItemCard
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AlarmUiState,
    onAdd: (AddAlarmMode) -> Unit,
    onEdit: (EditAlarmMode) -> Unit,
    onSort: (AlarmOrder) -> Unit,
    onSettings: () -> Unit,
    onAlarmEnableSwitch: (AlarmId) -> Unit
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
            MediumTopAppBar(
                title = {
                    Text(
                        text = if (topAppBarCollapsed) resources.getString(strings.alarm) else "Alarm info (wip)"
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onAdd(AddAlarmMode.AddAlarmToolbarAction)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add alalrm"
                        )
                    }
                    IconButton(
                        onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Manage drop down menu"
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }) {
                        if (editAvailable) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Edit"
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
                                        text = "Sort"
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
                                    text = "Settings"
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
        // TODO: Add no items handler
        LazyColumn(
            modifier = Modifier.padding(it),
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
        )
    }
}

