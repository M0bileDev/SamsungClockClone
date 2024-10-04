package com.example.samsungclockclone.presentation.activities.dismissAlarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_ID
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_MANAGER_ID
import com.example.samsungclockclone.framework.utils.strings
import com.example.samsungclockclone.presentation.theme.SamsungClockCloneTheme
import dagger.hilt.android.AndroidEntryPoint

// TODO: display alarm time
// TODO: dismiss alarm after onclick button action
@AndroidEntryPoint
class DismissAlarmActivity : ComponentActivity() {

    private val alarmId = intent?.getLongExtra(ALARM_ID, -1L) ?: -1L
    private val alarmManagerId = intent?.getLongExtra(ALARM_MANAGER_ID, -1L) ?: -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (alarmId == -1L || alarmManagerId == -1L) finish()

        createDismissScreen(
            getAlarmId = { alarmId },
            getAlarmManagerId = { alarmManagerId }
        )
    }

    private fun createDismissScreen(
        getAlarmId: () -> AlarmId,
        getAlarmManagerId: () -> AlarmManagerId
    ) {
        setContent {


            SamsungClockCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val dismissAlarmViewModel: DismissAlarmViewModel = hiltViewModel()
                    LaunchedEffect(LocalLifecycleOwner.current) {
                        dismissAlarmViewModel.loadAlarmData(
                            getAlarmId(),
                            getAlarmManagerId()
                        )
                    }

                    DismissAlarmScreen(
                        onDismiss = {
                            dismissAlarmViewModel.onDismiss(getAlarmManagerId())
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun DismissAlarmScreen(
        onDismiss: () -> Unit
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {


            Text("Ongoing alarm")
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(strings.dismiss))
            }
        }
    }

    @Preview
    @Composable
    private fun DismissAlarmScreenPreview() {
        SamsungClockCloneTheme {
            DismissAlarmScreen(
                onDismiss = {}
            )
        }
    }

}