@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
//    uiState: AlarmUiState,
    onAddAlarm: () -> Unit,
    onEdit: () -> Unit,
    onSort: () -> Unit,
    onSettings: () -> Unit
) {

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
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Edit"
                                )
                            },
                            onClick = onEdit
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Sort"
                                )
                            },
                            onClick = onSort
                        )
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
        Box(modifier = Modifier.padding(it))
    }
}

@Preview
@Composable
private fun AlarmScreenPreview() {
    SamsungClockCloneTheme {
        AlarmScreen(
            onAddAlarm = {},
            onEdit = {},
            onSort = {},
            onSettings = {}
        )
    }
}