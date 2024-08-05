package com.example.samsungclockclone.ui.customViews.dragAndDrop

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.ui.customModifier.translationYDragDropList
import kotlinx.coroutines.launch

@Composable
fun <T> DragAndDropLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    onMove: (Index, Index) -> Unit,
    content: @Composable (T) -> Unit,
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
            Box(
                modifier = Modifier.translationYDragDropList(
                    index = index,
                    dragAndDropListState = dragAndDropListState
                )
            ) {
                content(item)
            }
        }
    }
}

