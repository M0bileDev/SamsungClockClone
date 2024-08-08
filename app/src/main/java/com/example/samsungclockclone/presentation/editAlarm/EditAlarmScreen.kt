@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation.editAlarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.data.local.scheduler.AlarmId
import com.example.samsungclockclone.presentation.editAlarm.EditAlarmUiState.Companion.editAlarmUiStatePreview
import com.example.samsungclockclone.presentation.editAlarm.EditAlarmUiState.Companion.editAlarmUiStatePreview2
import com.example.samsungclockclone.presentation.editAlarm.EditAlarmUiState.Companion.editAlarmUiStatePreview3
import com.example.samsungclockclone.ui.customViews.EditAlarmItemCard
import com.example.samsungclockclone.ui.customViews.dragAndDrop.DragAndDropLazyColumn
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.drawables

@Composable
fun EditAlarmScreen(
    modifier: Modifier = Modifier,
    uiState: EditAlarmUiState,
    onSelectionAllChanged: () -> Unit,
    onSelectionChanged: (AlarmId) -> Unit,
    onTurnOn: () -> Unit,
    onTurnOff: () -> Unit,
    onDelete: () -> Unit,
    onDeleteAll: () -> Unit
) = with(uiState) {

    val topAppBarState = rememberTopAppBarState()
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        bottomBar = {
            EditAlarmBottomBar(
                onTurnOn = onTurnOn,
                onTurnOff = onTurnOff,
                onDelete = onDelete,
                onDeleteAll = onDeleteAll
            )
        },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = "Selected (wip)")
                },
                actions = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        RadioButton(
                            selected = allSelected,
                            onClick = onSelectionAllChanged
                        )
                        Text(text = "All")
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        }
    ) {

        var dragIconPress by remember { mutableStateOf(false) }

        DragAndDropLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            items = editAlarmItems,
            onMove = { _, _ ->
                // TODO: Here use extension function move MutableList<T>.move
            },
            onDragCondition = {
                dragIconPress
            },
            content = { contentValue, selected ->
                EditAlarmItemCard(
                    editAlarmItem = contentValue,
                    onSelectionChanged = onSelectionChanged,
                    onDragIconPress = { press -> dragIconPress = press }
                )
            }
        )
    }
}

@Composable
private fun EditAlarmUiState.EditAlarmBottomBar(
    onTurnOn: () -> Unit,
    onTurnOff: () -> Unit,
    onDelete: () -> Unit,
    onDeleteAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (turnOnEnabled) {
            TextButton(onClick = onTurnOn) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = drawables.ic_alarm_on),
                        contentDescription = "Turn on selected alarms"
                    )
                    Text("Turn on")
                }
            }
        }

        if (turnOffEnabled) {
            TextButton(onClick = onTurnOff) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = drawables.ic_alarm_off),
                        contentDescription = "Turn off selected alarms"
                    )
                    Text("Turn off")
                }
            }
        }

        if (deleteEnabled) {
            TextButton(onClick = onDelete) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = drawables.ic_delete),
                        contentDescription = "Delete selected alarms"
                    )
                    Text("Delete")
                }
            }
        }

        if (deleteAllEnabled) {
            TextButton(onClick = onDeleteAll) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = drawables.ic_delete),
                        contentDescription = "Delete all alarms"
                    )
                    Text("Delete all")
                }
            }
        }

    }
}

@Preview
@Composable
private fun EditAlarmScreenPreview() {
    SamsungClockCloneTheme {
        EditAlarmScreen(
            uiState = editAlarmUiStatePreview,
            onSelectionAllChanged = {},
            onSelectionChanged = {},
            onTurnOn = {},
            onTurnOff = {},
            onDelete = {},
            onDeleteAll = {}
        )
    }
}

@Preview
@Composable
private fun EditAlarmScreenPreview2() {
    SamsungClockCloneTheme {
        EditAlarmScreen(
            uiState = editAlarmUiStatePreview2,
            onSelectionAllChanged = {},
            onSelectionChanged = {},
            onTurnOn = {},
            onTurnOff = {},
            onDelete = {},
            onDeleteAll = {}
        )
    }
}

@Preview
@Composable
private fun EditAlarmScreenPreview3() {
    SamsungClockCloneTheme {
        EditAlarmScreen(
            uiState = editAlarmUiStatePreview3,
            onSelectionAllChanged = {},
            onSelectionChanged = {},
            onTurnOn = {},
            onTurnOff = {},
            onDelete = {},
            onDeleteAll = {}
        )
    }
}