package com.example.samsungclockclone.ui.customViews.dragAndDrop

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.getLazyListItemInfo
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.offsetBottom
import kotlinx.coroutines.Job
import androidx.compose.ui.geometry.Offset as GeometryOffset

typealias Index = Int
typealias Offset = Float

@Composable
fun rememberDragAndDropListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onMove: (Index, Index) -> Unit
): DragAndDropListState {
    return remember {
        DragAndDropListState(lazyListState, onMove)
    }
}

class DragAndDropListState(
    val lazyListState: LazyListState,
    private val onMove: (Index, Index) -> Unit
) {
    var overScrollJob by mutableStateOf<Job?>(null)

    private var draggedDistance by mutableFloatStateOf(0f)
    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    private val initiallyDragElementOffsets: Pair<Offset, Offset>?
        get() = initiallyDraggedElement?.let {
            Pair(it.offset + draggedDistance, it.offsetBottom + draggedDistance)
        }
    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let {
                lazyListState.getLazyListItemInfo(index = it)
            }
            ?.let { item ->
                (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
            }

    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set
    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getLazyListItemInfo(index = it)
        }


    fun onDragStart(offset: GeometryOffset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDragInterrupted() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overScrollJob?.cancel()
    }

    fun onDrag(offset: GeometryOffset) {
        draggedDistance += offset.y

        initiallyDragElementOffsets?.let { (topOffset, bottomOffset) ->

            currentElement?.let { hovered ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item ->
                        item.offsetBottom < topOffset || item.offset > bottomOffset || hovered.index == item.index
                    }
                    .firstOrNull { item ->
                        val delta = topOffset - hovered.offset
                        when {
                            delta > 0 -> (bottomOffset > item.offsetBottom)
                            else -> (topOffset < item.offset)
                        }
                    }?.also { item ->
                        currentIndexOfDraggedItem?.let { current ->
                            onMove.invoke(current, item.index)
                        }
                        currentIndexOfDraggedItem = item.index
                    }
            }
        }
    }

    fun onOverScroll(): Float {
        return initiallyDragElementOffsets?.let { (topOffset, bottomOffset) ->

            return@let when {
                draggedDistance > 0 -> (bottomOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff ->
                    diff > 0
                }

                draggedDistance < 0 -> (topOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff ->
                    diff < 0
                }

                else -> null
            }
        } ?: 0f
    }

}