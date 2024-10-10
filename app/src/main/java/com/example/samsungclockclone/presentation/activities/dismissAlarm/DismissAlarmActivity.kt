package com.example.samsungclockclone.presentation.activities.dismissAlarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.samsungclockclone.domain.`typealias`.AlarmId
import com.example.samsungclockclone.domain.`typealias`.AlarmManagerId
import com.example.samsungclockclone.ext.toDate
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_ID
import com.example.samsungclockclone.framework.receiver.AlarmReceiver.Companion.ALARM_MANAGER_ID
import com.example.samsungclockclone.framework.utils.strings
import com.example.samsungclockclone.presentation.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.presentation.utils.SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH_HOUR_MINUTE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DismissAlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alarmId = intent?.getLongExtra(ALARM_ID, -1L) ?: -1L
        val alarmManagerId = intent?.getLongExtra(ALARM_MANAGER_ID, -1L) ?: -1L

        if (alarmId == -1L || alarmManagerId == -1L) finish()

        createDismissScreen(
            getAlarmId = { alarmId },
            getAlarmManagerId = { alarmManagerId },
            onFinish = { finish() }
        )
    }


    private fun createDismissScreen(
        getAlarmId: () -> AlarmId,
        getAlarmManagerId: () -> AlarmManagerId,
        onFinish: () -> Unit
    ) {
        setContent {
            SamsungClockCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val dismissAlarmViewModel: DismissAlarmViewModel = hiltViewModel()
                    val dismissAlarmState by dismissAlarmViewModel.dismissAlarmState.collectAsState()

                    LaunchedEffect(LocalLifecycleOwner.current) {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            dismissAlarmViewModel.loadAlarmData(
                                getAlarmId(),
                                getAlarmManagerId()
                            )

                            dismissAlarmViewModel.dismissAlarmAction.collectLatest { action ->
                                when (action) {
                                    DismissAlarmViewModel.DismissAlarmAction.Finish -> onFinish()
                                }
                            }
                        }
                    }

                    DismissAlarmScreen(
                        dismissAlarmState = dismissAlarmState,
                        onDismiss = {
                            dismissAlarmViewModel.onDismiss(getAlarmManagerId())
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun DismissAlarmScreen(
        dismissAlarmState: DismissAlarmViewModel.DismissAlarmState,
        onDismiss: () -> Unit
    ) {

        when (dismissAlarmState) {
            is DismissAlarmViewModel.DismissAlarmState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is DismissAlarmViewModel.DismissAlarmState.Ongoing -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            dismissAlarmState.dismissAlarmUiState.fireTime.toDate(
                                SHORT_DAY_OF_WEEK_DAY_OF_MONTH_SHORT_MONTH_HOUR_MINUTE
                            )
                        )
                        Spacer(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                            dismissAlarmState.dismissAlarmUiState.name
                        )
                    }
                    Button(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(strings.dismiss))
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun DismissAlarmScreenPreview1() {
        SamsungClockCloneTheme {
            DismissAlarmScreen(
                DismissAlarmViewModel.DismissAlarmState.Idle,
                onDismiss = {}
            )
        }
    }

    @Preview
    @Composable
    fun DismissAlarmScreenPreview2() {
        SamsungClockCloneTheme {
            DismissAlarmScreen(
                DismissAlarmViewModel.DismissAlarmState.Ongoing(DismissAlarmUiState.previewDismissAlarmUiState),
                onDismiss = {}
            )
        }
    }

}