package com.example.samsungclockclone.ui.customViews.dragAndDrop

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.getLazyListItemInfo
import com.example.samsungclockclone.ui.customViews.dragAndDrop.ext.offsetBottom
import kotlinx.coroutines.Job

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

    var overScrollJob: Job? = null
    private var draggedElementDistance = 0f
    private var _draggedElementIndex = -1
        set(value) {
            field = value
            draggedElement = with(_draggedElementIndex) {
                if (value == -1) {
                    NotInitLazyListItemInfo()
                } else {
                    lazyListState.getLazyListItemInfo(this)
                }
            }
        }
    val draggedElementIndex: Int
        get() = _draggedElementIndex

    private val draggedElementOffset: Pair<Offset, Offset>
        get() = with(draggedElement) {
            Pair(
                offset.toFloat() + draggedElementDistance,
                offsetBottom.toFloat() + draggedElementDistance
            )
        }

    val draggedElementDisplacement
        get() = with(lazyListState) {
            val element = getLazyListItemInfo(_draggedElementIndex)
            draggedElement.offset.toFloat() + draggedElementDistance - element.offset
        }

    private var draggedElement: LazyListItemInfo = NotInitLazyListItemInfo()

    fun onDragStart(offset: androidx.compose.ui.geometry.Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .first { element ->
                offset.y.toInt() in element.offset..(element.offsetBottom)
            }.also {
                _draggedElementIndex = it.index
            }
    }

    fun onDragInterrupted() {
        draggedElementDistance = 0f
        _draggedElementIndex = -1
        overScrollJob?.cancel()
    }

    fun onDrag(offset: androidx.compose.ui.geometry.Offset) {
        draggedElementDistance += offset.y

        val (topOffset, bottomOffset) = draggedElementOffset
        lazyListState
            .layoutInfo
            .visibleItemsInfo
            .filterNot { element ->
                element.offsetBottom < topOffset || element.offset > bottomOffset || draggedElement.index == element.index
            }.first { element ->
                val delta = topOffset - draggedElement.offset
                when {
                    delta > 0 -> {
                        bottomOffset > element.offsetBottom
                    }

                    else -> {
                        topOffset < element.offset
                    }
                }
            }.also { element ->
                onMove(_draggedElementIndex, element.index)
                _draggedElementIndex = element.index
            }
    }

    fun onOverScroll(): Float {
        return with(draggedElementOffset) {
            val (topOffset, bottomOffset) = draggedElementOffset
            val viewportEndOffset = lazyListState.layoutInfo.viewportEndOffset
            val viewportStartOffset = lazyListState.layoutInfo.viewportStartOffset
            when {
                draggedElementDistance > 0 -> {
                    (bottomOffset - viewportEndOffset).takeIf { diff -> diff > 0 }
                }

                draggedElementDistance < 0 -> {
                    (topOffset - viewportStartOffset).takeIf { diff -> diff < 0 }
                }

                else -> null
            } ?: 0f
        }
    }

}

class NotInitLazyListItemInfo : LazyListItemInfo {
    override val index: Int
        get() = throw IllegalStateException("Index not initialize. You have to use initialized object!")
    override val key: Any
        get() = throw IllegalStateException("Key not initialize. You have to use initialized object!")
    override val offset: Int
        get() = throw IllegalStateException("Offset not initialize. You have to use initialized object!")
    override val size: Int
        get() = throw IllegalStateException("Size not initialize. You have to use initialized object!")

}