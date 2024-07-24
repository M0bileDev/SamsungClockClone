@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.ui.customViews.AlarmItemCard
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
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
        // TODO: Add no items handler
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(alarmItems) { item ->
                AlarmItemCard(alarmItem = item, onChanged = {})
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

