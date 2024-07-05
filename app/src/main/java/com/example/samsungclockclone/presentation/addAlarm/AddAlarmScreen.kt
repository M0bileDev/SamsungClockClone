package com.example.samsungclockclone.presentation.addAlarm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.samsungclockclone.data.utils.TimeFormat
import com.example.samsungclockclone.ui.customViews.HorizontalChipGroup
import com.example.samsungclockclone.ui.customViews.SwipeableClock
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme


@Composable
fun AddAlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AddAlarmUiState,
    onSelectedDaysOfWeek: (String) -> Unit
) = with(uiState) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            SwipeableClock(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                timeFormat = TimeFormat.Hours
            ) {

            }
            Text(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically),
                fontSize = 40.sp,
                text = ":"
            )
            SwipeableClock(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                timeFormat = TimeFormat.Minutes
            ) {

            }
        }
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start),
                        text = "Tomorrow-Sat, 6 Jul"
                    )
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.End),
                        onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date from calendar"
                        )
                    }
                }
                HorizontalChipGroup(
                    modifier = Modifier.fillMaxWidth(),
                    items = daysOfWeek,
                    selectedItems = selectedDaysOfWeek,
                    onSelected = onSelectedDaysOfWeek
                )
            }
        }
    }
}


@Preview
@Composable
private fun AddAlarmPreview() {
    SamsungClockCloneTheme {
        AddAlarmScreen(
            modifier = Modifier.fillMaxSize(),
            AddAlarmUiState(
                listOf("M","T","W","T","F","S","S"),
                listOf("M")
            ),
            onSelectedDaysOfWeek = {})
    }
}





