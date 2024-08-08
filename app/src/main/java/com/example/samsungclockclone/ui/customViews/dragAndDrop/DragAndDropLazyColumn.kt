package com.example.samsungclockclone.ui.customViews.dragAndDrop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.ui.customModifier.translationYDragAndDrop
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.move
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import kotlinx.coroutines.launch

@Composable
fun <T> DragAndDropLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    onMove: (Index, Index) -> Unit,
    content: @Composable (T, Boolean) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
) {

    val coroutineScope = rememberCoroutineScope()
    val dragAndDropListState = rememberDragAndDropListState(onMove = onMove)

    LazyColumn(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = dragAndDropListState::onDragStart,
                    onDrag = { change, offset ->
                        change.consume()
                        with(dragAndDropListState) {
                            onDrag(offset)
                            onOverScroll()
                                .takeIf { it != 0f }
                                ?.let { difference ->
                                    overScrollJob = coroutineScope.launch {
                                        lazyListState.scrollBy(difference)
                                    }
                                } ?: overScrollJob?.cancel()
                        }
                    },
                    onDragEnd = dragAndDropListState::onDragInterrupted,
                    onDragCancel = dragAndDropListState::onDragInterrupted
                )
            },
        state = dragAndDropListState.lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled
    ) {
        itemsIndexed(items) { index, item ->
            val selected by remember {
                derivedStateOf {
                    dragAndDropListState.currentIndexOfDraggedItem?.let { it == index } ?: false
                }
            }
            Box(
                modifier = Modifier.translationYDragAndDrop(
                    index = index,
                    dragAndDropListState = dragAndDropListState
                )
            ) {
                content(item, selected)
            }
        }
    }
}

@Preview
@Composable
private fun DragAndDropListStatePreview() {
    SamsungClockCloneTheme {

        val editableList = remember {
            mutableListOf("First", "Second", "Third")
        }

        DragAndDropLazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            items = editableList,
            onMove = { fromIndex, toIndex -> editableList.move(fromIndex, toIndex) },
            content = { text, selected ->
                Card(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    border = if (selected) BorderStroke(2.dp, Color.Red) else null
                ) {
                    Text(text = text)
                }
            }
        )
    }
}

