package com.example.samsungclockclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.samsungclockclone.navigation.NavigationUtils.navBottomItems
import com.example.samsungclockclone.navigation.Screens
import com.example.samsungclockclone.presentation.addAlarm.AddAlarmScreen
import com.example.samsungclockclone.presentation.addAlarm.AddAlarmViewModel
import com.example.samsungclockclone.presentation.alarm.AlarmScreen
import com.example.samsungclockclone.presentation.alarm.AlarmViewModel
import com.example.samsungclockclone.ui.theme.SamsungClockCloneTheme

class MainActivity : ComponentActivity() {
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
                            NavigationBar {
                                navBottomItems.forEach { screen ->
                                    NavigationBarItem(
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
                                        icon = {},
                                        label = {
                                            Text(text = resources.getString(screen.name))
                                        }
                                    )

                                }
                            }
                        }
                    ) { padding ->
                        NavHost(
                            modifier = Modifier.padding(padding),
                            navController = navController,
                            startDestination = navBottomItems.first().route
                        ) {

                            composable(Screens.AddAlarm.route) {
                                val addAlarmViewModel: AddAlarmViewModel by viewModels()
                                val uiState by addAlarmViewModel.uiState.collectAsState()

                                AddAlarmScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    uiState = uiState,
                                    onSelectedDaysOfWeek = {}
                                )
                            }

                            composable(Screens.Alarm.route) {
                                val alarmViewModel: AlarmViewModel by viewModels()
                                AlarmScreen(
                                    onAddAlarm = {},
                                    onEdit = {},
                                    onSort = {},
                                    onSettings = {}
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