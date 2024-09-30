package com.example.samsungclockclone.presentation.customs.dragAndDrop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.presentation.utils.translationYDragAndDrop
import com.example.samsungclockclone.presentation.customs.dragAndDrop.ext.move
import com.example.samsungclockclone.presentation.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.framework.utils.drawables
import kotlinx.coroutines.launch

@Composable
fun <T> DragAndDropLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    content: @Composable (T, Boolean) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    onMove: (Index, Index) -> Unit,
    onDragCondition: () -> Boolean = { true }
) {

    val coroutineScope = rememberCoroutineScope()
    val dragAndDropListState = rememberDragAndDropListState(
        onMove = onMove,
        onDragCondition = onDragCondition
    )

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

        var iconPress by remember {
            mutableStateOf(false)
        }

        DragAndDropLazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            items = editableList,
            onMove = { fromIndex, toIndex -> editableList.move(fromIndex, toIndex) },
            onDragCondition = {
                iconPress
            },
            content = { text, selected ->
                Card(
                    border = if (selected) BorderStroke(2.dp, Color.Red) else null
                ) {
                    Row(
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            iconPress = event.type != PointerEventType.Release
                                        }
                                    }
                                },
                            painter = painterResource(id = drawables.ic_drag),
                            contentDescription = null
                        )
                        Text(text = text)
                    }
                }
            }
        )
    }
}

