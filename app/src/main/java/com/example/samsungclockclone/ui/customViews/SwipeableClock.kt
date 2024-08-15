@file:OptIn(ExperimentalFoundationApi::class)

package com.example.samsungclockclone.ui.customViews

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.samsungclockclone.data.utils.TimeFormat
import com.example.samsungclockclone.data.utils.convertTimeFormatToString
import com.example.samsungclockclone.data.utils.formatTimeValue
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import kotlinx.coroutines.delay

/**
 * Swipable clock widget. Parameters value are created to show at once max
 * three elements in column.
 */
@Composable
fun SwipeableClock(
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat,
    clockCount: Int = 1000,
    clockStartPoint: Int = clockCount / 2,
    swipeableAreaHeight: Dp = 300.dp,
    swipeableAreaWidth: Dp = 150.dp,
    itemsPadding: Dp = swipeableAreaHeight / 3,
    itemSize: Dp = swipeableAreaHeight / 3,
    itemsFlungAtOnce: Int = 12,
    fontSize: TextUnit = 40.sp,
    onValueChanged: (Int) -> Unit,
    onMoveToValue: () -> Int?
) {

    val pagerState = rememberPagerState(pageCount = {
        clockCount
    })

    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(itemsFlungAtOnce)
    )

    LaunchedEffect(key1 = pagerState, key2 = onMoveToValue) {
        val moveToValue = onMoveToValue() ?: 0
        val page = clockStartPoint - moveToValue
        pagerState.scrollToPage(clockStartPoint)
        delay(300)
        pagerState.animateScrollToPage(page)
    }

    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            val formattedTimeValue =
                formatTimeValue(it % timeFormat.format - (clockStartPoint), timeFormat)
            onValueChanged(formattedTimeValue)
        }
    }

    Box(modifier = modifier) {
        VerticalPager(
            modifier = Modifier
                .height(swipeableAreaHeight)
                .width(swipeableAreaWidth),
            state = pagerState,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = itemsPadding),
        ) {
            Box(
                modifier = Modifier.size(itemSize),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    fontSize = fontSize,
                    text = convertTimeFormatToString(
                        it % timeFormat.format - (clockStartPoint),
                        timeFormat
                    )
                )
            }
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
            onMoveToValue = { 0 },
            onValueChanged = {}
        )
    }
}