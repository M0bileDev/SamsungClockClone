@file:OptIn(ExperimentalFoundationApi::class)

package com.example.samsungclockclone.ui.customViews

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.data.utils.TimeFormat
import com.example.samsungclockclone.data.utils.convertTimeFormatToString
import com.example.samsungclockclone.data.utils.formatTimeValue
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

const val PAGER_COUNT = 1000
const val START_PAGE = PAGER_COUNT / 2

@Composable
fun SwipeableClock(modifier: Modifier, timeFormat: TimeFormat, onValueChanged: (Int) -> Unit) {

    val pagerState = rememberPagerState(pageCount = {
        PAGER_COUNT
    })
    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(12)
    )

    LaunchedEffect(key1 = pagerState) {
        pagerState.scrollToPage(START_PAGE)
    }

    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            val formattedTimeValue =
                formatTimeValue(it % timeFormat.format - (START_PAGE), timeFormat)
            onValueChanged(formattedTimeValue)
        }
    }

    Box(modifier = modifier) {
        VerticalPager(
            modifier = Modifier
                .height(200.dp)
                .width(150.dp),
            state = pagerState,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = 70.dp)
        ) {
            Text(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Green),
                text = convertTimeFormatToString(it % timeFormat.format - (START_PAGE), timeFormat)
            )
        }
    }
}

@Preview
@Composable
private fun SwipeableClockPreview() {
    SamsungClockCloneTheme {
        SwipeableClock(
            modifier = Modifier.fillMaxSize(),
            timeFormat = TimeFormat.Hours,
            onValueChanged = {}
        )
    }
}