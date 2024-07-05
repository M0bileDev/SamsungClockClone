package com.example.samsungclockclone.ui.customViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

@Composable
fun <T> HorizontalChipGroup(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItems: List<T>,
    onSelected: (T) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        items.forEach { item ->
            RoundedChip(
                value = item,
                selected = selectedItems.contains(item),
                onSelected = onSelected
            )
        }
    }
}

@Preview
@Composable
private fun HorizontalChipGroupPreview() {
    SamsungClockCloneTheme {
        HorizontalChipGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf("A", "B", "C",),
            selectedItems = listOf("A", "C"),
            onSelected = {})
    }
}