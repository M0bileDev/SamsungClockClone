package com.example.samsungclockclone.ui.customViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.samsungclockclone.presentation.addAlarm.ShortName
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

@Composable
fun <T : ShortName> RoundedChip(
    value: T,
    selected: Boolean,
    size: Dp = 40.dp,
    textSize: TextUnit = 16.sp,
    shortNameValue: Int = 1,
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

    FilterChip(
        modifier = Modifier.size(size),
        colors = FilterChipDefaults.filterChipColors().copy(
            labelColor = backgroundColor
        ),
        shape = CircleShape,
        border = border,
        selected = selected,
        label = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    fontSize = textSize,
                    textAlign = TextAlign.Center,
                    text = stringResource(id = value.nameResourceValue).uppercase()
                        .take(shortNameValue),
                    color = textColor
                )
            }
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
            value = object : ShortName {
                override val nameResourceValue: Int
                    get() = android.R.string.unknownName

            },
            selected = true,
            onSelected = {}
        )
    }
}