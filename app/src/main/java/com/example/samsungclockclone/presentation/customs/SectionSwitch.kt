package com.example.samsungclockclone.presentation.customs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samsungclockclone.presentation.theme.SamsungClockCloneTheme

@Composable
fun SectionSwitch(
    header: String,
    body: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = header)
            Text(text = body)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.End)
                .height(IntrinsicSize.Min)
        ) {
            VerticalDivider(modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 8.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Preview
@Composable
private fun SectionSwitchPreview() {
    SamsungClockCloneTheme {
        SectionSwitch(
            header = "Header",
            body = "Body",
            checked = true,
            onCheckedChange = {}
        )
    }
}