@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation.editAlarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.data.local.scheduler.AlarmId
import com.example.samsungclockclone.presentation.editAlarm.EditAlarmUiState.Companion.editAlarmUiStatePreview
import com.example.samsungclockclone.ui.customViews.EditAlarmItemCard
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

@Composable
fun EditAlarmScreen(
    modifier: Modifier = Modifier,
    uiState: EditAlarmUiState,
    onSelectionAllChanged: () -> Unit,
    onSelectionChanged: (AlarmId) -> Unit
) = with(uiState) {

    val topAppBarState = rememberTopAppBarState()
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(editAlarmItems) { item ->
                EditAlarmItemCard(
                    editAlarmItem = item,
                    onSelectionChanged = onSelectionChanged
                )
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
            onSelectionChanged = {}
        )
    }
}