package com.example.samsungclockclone.presentation.customs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.samsungclockclone.domain.model.addAlarm.NameResource
import com.example.samsungclockclone.framework.utils.strings
import com.example.samsungclockclone.presentation.utils.drawPointerAbove
import com.example.samsungclockclone.presentation.theme.SamsungClockCloneTheme

@Composable
fun <T : NameResource> PointerSelectedItems(
    modifier: Modifier = Modifier,
    pointerColor: Color = MaterialTheme.colorScheme.primary,
    textSelectedColor:Color = MaterialTheme.colorScheme.primary,
    textColor:Color = MaterialTheme.colorScheme.onSurface,
    pointerRadius: Float = 4f,
    items: List<T>,
    selectedItems: List<T>,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        items.forEach { item ->
            val visible = selectedItems.any { it.nameResourceValue == item.nameResourceValue }
            Text(
                color = if(visible) textSelectedColor else textColor,
                modifier = Modifier.drawPointerAbove(visible, pointerRadius, pointerColor),
                text = stringResource(id = item.nameResourceValue).take(1)
            )
        }
    }
}

@Preview
@Composable
private fun PointerSelectedItemsPreview() {

    val selected = object : NameResource {
        override val nameResourceValue: Int
            get() = android.R.string.ok
    }

    SamsungClockCloneTheme {
        PointerSelectedItems(
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
        )
    }
}