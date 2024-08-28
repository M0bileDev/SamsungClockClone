@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.samsungclockclone.presentation

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.samsungclockclone.data.receiver.TimeTickReceiver
import com.example.samsungclockclone.domain.ticker.TimeTicker
import com.example.samsungclockclone.navigation.NavigationUtils.navBottomItems
import com.example.samsungclockclone.navigation.Screens
import com.example.samsungclockclone.presentation.addAlarm.AddAlarmScreen
import com.example.samsungclockclone.presentation.addAlarm.AddAlarmViewModel
import com.example.samsungclockclone.presentation.alarm.AlarmScreen
import com.example.samsungclockclone.presentation.alarm.AlarmViewModel
import com.example.samsungclockclone.presentation.editAlarm.EditAlarmScreen
import com.example.samsungclockclone.presentation.editAlarm.EditAlarmViewModel
import com.example.samsungclockclone.presentation.editAlarm.utils.ALARM_ID_KEY
import com.example.samsungclockclone.ui.customModifier.drawUnderline
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme
import com.example.samsungclockclone.usecase.UpdateAlarmMangersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var scopedJob: Job? = null
    private val job: Job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Default)

    @Inject
    lateinit var ticker: TimeTicker

    @Inject
    lateinit var updateAlarmMangersUseCase: UpdateAlarmMangersUseCase

    private val tickerReceiver = TimeTickReceiver()

    override fun onResume() {
        super.onResume()
        scopedJob?.cancel()
        scopedJob = coroutineScope.launch {
            updateAlarmMangersUseCase(this)
        }
        registerReceiver(tickerReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(tickerReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        ticker.onDestroy()
        job.cancel()
        scopedJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SamsungClockCloneApplication()
    }

    private fun SamsungClockCloneApplication() {
        setContent {

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            SamsungClockCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {

                            if (hideNavigationBar(currentDestination)) return@Scaffold

                            NavigationBar {
                                navBottomItems.forEach { screen ->
                                    val selected =
                                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(
                                                    navController.graph.findStartDestination().id
                                                ) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            val color = MaterialTheme.colorScheme.onSurface
                                            Text(
                                                modifier = Modifier.drawUnderline(
                                                    selected,
                                                    color = color
                                                ),
                                                text = resources.getString(screen.name),
                                                fontWeight = if (selected) FontWeight.Bold else null,
                                            )
                                        }
                                    )

                                }
                            }
                        }
                    ) { padding ->
                        NavHost(
                            modifier = Modifier.padding(padding),
                            navController = navController,
                            startDestination = Screens.Alarm.route
                        ) {


                            composable(
                                "${Screens.AddAlarm.route}/{${ALARM_ID_KEY}}",
                                arguments = listOf(navArgument(ALARM_ID_KEY) {
                                    type = NavType.LongType
                                })
                            ) {
                                val addAlarmViewModel: AddAlarmViewModel = hiltViewModel()
                                val uiState by addAlarmViewModel.uiState.collectAsState()
                                val localDate by remember {
                                    mutableStateOf(LocalDateTime.now())
                                }
                                val datePickerState = rememberDatePickerState(
                                    yearRange = IntRange(
                                        localDate.year,
                                        localDate.year + 1
                                    ),
                                    initialSelectedDateMillis = localDate
                                        .plusDays(1)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli(),
                                    selectableDates = object : SelectableDates {
                                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                            return utcTimeMillis >= localDate
                                                .plusDays(1)
                                                .withHour(0)
                                                .withMinute(0)
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli()
                                        }
                                    })

                                val lifecycle = LocalLifecycleOwner.current
                                LaunchedEffect(key1 = lifecycle) {
                                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                        addAlarmViewModel.actions.collectLatest { action ->
                                            when (action) {
                                                AddAlarmViewModel.AddAlarmAction.ScheduleCompleted -> {
                                                    navController.navigateUp()
                                                }

                                                AddAlarmViewModel.AddAlarmAction.RequestSchedulePermission -> {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                        startActivity(
                                                            Intent(
                                                                ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                                            )
                                                        )
                                                    }
                                                }

                                                AddAlarmViewModel.AddAlarmAction.NavigateBack -> {
                                                    navController.navigateUp()
                                                }
                                            }
                                        }
                                    }
                                }

                                AddAlarmScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    uiState = uiState,
                                    datePickerState = datePickerState,
                                    onHourChanged = addAlarmViewModel::hourChanged,
                                    onMoveToHour = addAlarmViewModel::onMoveToHour,
                                    onMinuteChanged = addAlarmViewModel::minuteChanged,
                                    onMoveToMinute = addAlarmViewModel::onMoveToMinute,
                                    onDateChanged = addAlarmViewModel::onDateChanged,
                                    onDayOfWeekChanged = addAlarmViewModel::dayOfWeekChanged,
                                    onNameChanged = addAlarmViewModel::nameChanged,
                                    onCancel = navController::navigateUp,
                                    onSave = addAlarmViewModel::onSave,
                                    onDismissRequest = addAlarmViewModel::dismissSchedulePermission,
                                    onRequestSchedulePermission = addAlarmViewModel::onRequestSchedulePermission,
                                    onDisplayDatePicker = addAlarmViewModel::onDisplayDatePicker,
                                    onDismissDatePicker = addAlarmViewModel::onDismissDatePicker
                                )
                            }

                            composable(Screens.Alarm.route) {
                                val alarmViewModel: AlarmViewModel by viewModels()
                                val uiState by alarmViewModel.uiState.collectAsState()

                                val lifecycle = LocalLifecycleOwner.current
                                LaunchedEffect(key1 = lifecycle) {
                                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                        alarmViewModel.actions.collectLatest { action ->
                                            when (action) {
                                                is AlarmViewModel.AlarmAction.EditAlarm -> {
                                                    navController.navigate("${Screens.EditAlarm.route}/${action.alarmId}")
                                                }

                                                is AlarmViewModel.AlarmAction.AddAlarm -> {
                                                    navController.navigate("${Screens.AddAlarm.route}/${action.alarmId}")
                                                }

                                            }
                                        }
                                    }
                                }

                                AlarmScreen(
                                    uiState = uiState,
                                    onAdd = alarmViewModel::onAdd,
                                    onEdit = alarmViewModel::onEdit,
                                    onSort = alarmViewModel::onSort,
                                    onSettings = {},
                                    onAlarmEnableSwitch = alarmViewModel::onAlarmEnableSwitch
                                )
                            }

                            composable(
                                route = "${Screens.EditAlarm.route}/{${ALARM_ID_KEY}}",
                                arguments = listOf(navArgument(ALARM_ID_KEY) {
                                    type = NavType.LongType
                                })
                            ) {
                                val editAlarmViewModel: EditAlarmViewModel = hiltViewModel()
                                val uiState by editAlarmViewModel.uiState.collectAsState()

                                val lifecycle = LocalLifecycleOwner.current
                                LaunchedEffect(key1 = lifecycle) {
                                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                        editAlarmViewModel.actions.collectLatest { action ->
                                            when (action) {
                                                EditAlarmViewModel.EditAlarmAction.NavigateBack -> {
                                                    navController.navigateUp()
                                                }
                                            }
                                        }
                                    }
                                }

                                EditAlarmScreen(
                                    uiState = uiState,
                                    onSelectionAllChanged = editAlarmViewModel::onSelectionAllChanged,
                                    onSelectionChanged = editAlarmViewModel::onSelectionChanged,
                                    onTurnOn = editAlarmViewModel::onTurnOn,
                                    onTurnOff = editAlarmViewModel::onTurnOff,
                                    onDelete = editAlarmViewModel::onDelete,
                                    onDeleteAll = editAlarmViewModel::onDeleteAll,
                                    onMove = editAlarmViewModel::onMove,
                                    onMoveCompleted = editAlarmViewModel::onMoveCompleted
                                )
                            }

                            composable(Screens.Stopwatch.route) {

                            }
                            composable(Screens.Timer.route) {

                            }
                        }
                    }

                }
            }
        }
    }
}

private fun hideNavigationBar(currentDestination: NavDestination?): Boolean {
    return !navBottomItems.any { screen ->
        currentDestination?.hierarchy?.any { destination -> screen.route == destination.route }
            ?: false
    }
}

