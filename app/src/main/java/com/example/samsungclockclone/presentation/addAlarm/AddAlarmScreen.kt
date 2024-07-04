@file:OptIn(ExperimentalFoundationApi::class)

package com.example.samsungclockclone.presentation.addAlarm

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme


@Composable
fun AddAlarmScreen(
    modifier: Modifier = Modifier,
//    uiState: AddAlarmUiState
) {


}

@Preview
@Composable
private fun AddAlarmPreview() {
    SamsungClockCloneTheme {
        AddAlarmScreen(modifier = Modifier.fillMaxSize())
    }
}


