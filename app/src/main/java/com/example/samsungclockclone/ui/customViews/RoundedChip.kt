package com.example.samsungclockclone.ui.customViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

@Composable
fun <T> RoundedChip(
    value: T,
    selected: Boolean,
    size: Dp = 40.dp,
    selectedLabelColor: Color = MaterialTheme.colorScheme.onSurface,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.surface,
    selectedTextColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedBorder: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    unselectedBorder: BorderStroke? = null,
    onSelected: (T) -> Unit,
) {

    val backgroundColor = if (selected) {
        selectedLabelColor
    } else {
        unselectedLabelColor
    }

    val textColor = if (selected) {
        selectedTextColor
    } else {
        unselectedTextColor
    }

    val border = if (selected) {
        selectedBorder
    } else {
        unselectedBorder
    }

    FilterChip(colors = FilterChipDefaults.filterChipColors().copy(
        labelColor = backgroundColor
    ),
        modifier = Modifier.size(size),
        shape = CircleShape,
        border = border,
        selected = selected,
        label = {
            Text(
                text = value.toString(),
                color = textColor
            )
        },
        onClick = {
            onSelected(value)
        })
}

@Preview
@Composable
private fun RoundedChipPreview() {
    SamsungClockCloneTheme {
        RoundedChip(
            value = "M",
            selected = true,
            onSelected = {}
        )
    }
}