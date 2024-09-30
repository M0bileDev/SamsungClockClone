package com.example.samsungclockclone.presentation.customs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.samsungclockclone.domain.model.addAlarm.NameResource
import com.example.samsungclockclone.framework.utils.strings
import com.example.samsungclockclone.presentation.theme.SamsungClockCloneTheme

@Composable
fun <T : NameResource> HorizontalChipGroup(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItems: List<T>,
    onSelected: (T) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        items.forEach { item ->
            RoundedChip(
                value = item,
                selected = selectedItems.any { it.nameResourceValue == item.nameResourceValue },
                onSelected = onSelected
            )
        }
    }
}

@Preview
@Composable
private fun HorizontalChipGroupPreview() {

    val selected = object : NameResource {
        override val nameResourceValue: Int
            get() = android.R.string.ok
    }

    SamsungClockCloneTheme {
        HorizontalChipGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf(
                object : NameResource {
                    override val nameResourceValue: Int
                        get() = strings.unknownname
                },
                selected,
                object : NameResource {
                    override val nameResourceValue: Int
                        get() = strings.unknownname
                }),

            selectedItems = listOf(selected),
            onSelected = {})
    }
}