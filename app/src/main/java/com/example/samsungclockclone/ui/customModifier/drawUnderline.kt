package com.example.samsungclockclone.ui.customModifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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