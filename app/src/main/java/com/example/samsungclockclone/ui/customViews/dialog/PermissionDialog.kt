package com.example.samsungclockclone.ui.customViews.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

// TODO: extract strings

@Composable
fun PermissionDialog(
    dialogText: String,
    onRequestPermission: (Boolean) -> Unit,
    onDismiss: (Boolean) -> Unit
) {

    var checkStatus by remember {
        mutableStateOf(false)
    }

    Dialog(onDismissRequest = { onDismiss(checkStatus) }) {
        Surface(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = dialogText)
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = { onRequestPermission(checkStatus) }
                    ) {
                        Text(text = "Yes")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = { onDismiss(checkStatus) }
                    ) {
                        Text(text = "No")
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checkStatus,
                        onCheckedChange = { checkStatus = !checkStatus })
                    Text(text = "Never ask again")
                }
            }
        }
    }

}

@Preview
@Composable
private fun PermissionDialogPreview() {
    SamsungClockCloneTheme {
        PermissionDialog(
            dialogText = "Permission to display notification is mandatory for core functionality.\n\nIt will provide information of ongoing alarms.\n\nBy default this permission is denied.\nDo you want to grant this permission?",
            onRequestPermission = {},
            onDismiss = {})
    }
}

@Composable
fun ShortInfoDialog(
    dialogText: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = dialogText)
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onDismiss
                ) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Preview
@Composable
private fun PermissionDeniedDialogPreview() {
    SamsungClockCloneTheme {
        ShortInfoDialog(
            dialogText = "You can always change application permissions in system settings.",
            onDismiss = {}
        )
    }
}