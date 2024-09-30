package com.example.samsungclockclone.presentation.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.samsungclockclone.presentation.customs.dragAndDrop.DragAndDropListState

fun Modifier.drawUnderline(
    visible: Boolean,
    strokeWidth: Float = 1f,
    offset: TextUnit = 0.sp,
    color: Color
) = this.then(
    drawBehind {
        if (!visible) return@drawBehind

        val strokeWidthPx = strokeWidth.dp.toPx()
        val verticalOffset = size.height + offset.toPx()

        drawLine(
            color = color,
            strokeWidth = strokeWidthPx,
            start = Offset(0f, verticalOffset),
            end = Offset(size.width, verticalOffset)
        )
    }
)

@Preview
@Composable
private fun DrawUnderlinePreview() {
    Text(
        modifier = Modifier.drawUnderline(
            visible = true,
            color = Color.Black
        ),
        text = "Preview"
    )
}

fun Modifier.drawPointerAbove(
    visible: Boolean,
    radius: Float = 4f,
    color: Color
) = this
    .padding(top = 8.dp)
    .then(
        drawBehind {
            if (!visible) return@drawBehind

            drawCircle(
                color = color,
                center = Offset(size.width / 2, -(size.height) / 4),
                radius = radius,
            )
        }
    )

@Preview
@Composable
private fun DrawPointerAbovePreview() {
    Text(
        modifier = Modifier.drawPointerAbove(
            visible = true,
            color = Color.Black
        ),
        text = "Preview"
    )
}

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.translationYDragAndDrop(
    index: Int,
    dragAndDropListState: DragAndDropListState
): Modifier {
    val offsetOrNull = dragAndDropListState.elementDisplacement.takeIf {
        index == dragAndDropListState.currentIndexOfDraggedItem
    }
    return this then Modifier.graphicsLayer {
        translationY = offsetOrNull ?: 0f
    }
}