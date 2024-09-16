package com.example.samsungclockclone.ui.customViews.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.ui.utils.strings

@Composable
fun ShortInfoDialog(
    dialogText: String,
    actionText: String = "",
    onAction: () -> Unit = {},
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
                if (actionText.isNotEmpty()) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = onAction
                    ) {
                        Text(text = actionText)
                    }
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                }
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onDismiss
                ) {
                    Text(text = stringResource(id = strings.dismiss))
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
            dialogText = "Short info dialog",
            actionText = "Action text",
            onAction = {},
            onDismiss = {}
        )
    }
}