package com.example.samsungclockclone.application

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.samsungclockclone.framework.receiver.TimeTickReceiver
import com.example.samsungclockclone.framework.dialog.DialogListener
import com.example.samsungclockclone.framework.notification.NotificationBuilder
import com.example.samsungclockclone.framework.permissions.PermissionsListener
import com.example.samsungclockclone.framework.preferences.SelectionPreferences
import com.example.samsungclockclone.framework.ticker.TimeTicker
import com.example.samsungclockclone.presentation.main.MainActivity
import com.example.samsungclockclone.usecase.UpdateAlarmMangersUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltAndroidApp
class SamsungClockCloneApplication : Application(), ActivityLifecycleCallbacks {

    @Inject
    lateinit var timeTicker: TimeTicker

    @Inject
    lateinit var updateAlarmMangersUseCase: UpdateAlarmMangersUseCase

    @Inject
    lateinit var selectionPreferences: SelectionPreferences

    @Inject
    lateinit var dialogListener: DialogListener

    @Inject
    lateinit var permissionsListener: PermissionsListener

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    private val timeTickReceiver = TimeTickReceiver()

    private var updateAlarmMangersJob: Job? = null
    private val updateAlarmMangersCoroutineScope = CoroutineScope(Dispatchers.Default)

    private var postNotificationPermissionManagerJob: Job? = null
    private val userSelectionCoroutineScope = CoroutineScope(Dispatchers.Default)

    private var postNotificationPermissionJob: Job? = null
    private val permissionListenerCoroutineScope = CoroutineScope(Dispatchers.Default)

    private var onPermissionGranted: () -> Unit = {}
    private var onPermissionDenied: () -> Unit = {}
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        requestPermissionLauncher = (activity as MainActivity).registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

        startPostNotificationPermissionListener()
        startPostNotificationPermissionManager(activity)
        notificationBuilder.createAlarmNotificationChannel()
    }

    private fun startPostNotificationPermissionListener() {
        postNotificationPermissionJob?.cancel()
        postNotificationPermissionJob = permissionListenerCoroutineScope.launch {
            permissionsListener.collectPermissionPostNotification().collectLatest {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    runPermission(
                        onGranted = {
                            // No-op
                        },
                        onDenied = {
                            this.launch {
                                dialogListener.changedVisibilityShortInfoDialog(true)
                            }
                        },
                        permission = Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
    }

    private fun runPermission(onGranted: () -> Unit, onDenied: () -> Unit, permission: String) {
        onPermissionGranted = onGranted
        onPermissionDenied = onDenied
        requestPermissionLauncher.launch(permission)
    }

    private fun startPostNotificationPermissionManager(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            postNotificationPermissionManagerJob?.cancel()
            postNotificationPermissionManagerJob = userSelectionCoroutineScope.launch {
                val notificationPermissionAskAgainEnabled =
                    selectionPreferences.collectNotificationPermissionAskAgainEnabled().first()
                withContext(Dispatchers.Main) {

                    if (notificationPermissionAskAgainEnabled) {
                        when {
                            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                                //no-op
                            }

                            ActivityCompat.shouldShowRequestPermissionRationale(
                                activity, Manifest.permission.POST_NOTIFICATIONS
                            ) -> {
                                withContext(Dispatchers.Default) {
                                    dialogListener.changedVisibilityPermissionPostNotificationDialog(
                                        true
                                    )
                                }
                            }

                            else -> withContext(Dispatchers.Default) {
                                dialogListener.changedVisibilityPermissionPostNotificationDialog(
                                    true
                                )
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
//        no-op
    }

    override fun onActivityResumed(activity: Activity) {
        updateAlarmMangersJob?.cancel()
        updateAlarmMangersJob = updateAlarmMangersCoroutineScope.launch {
            updateAlarmMangersUseCase(this)
        }
        registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onActivityPaused(activity: Activity) {
        unregisterReceiver(timeTickReceiver)
    }

    override fun onActivityStopped(activity: Activity) {
//        no-op
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//        no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        postNotificationPermissionManagerJob?.cancel()
        postNotificationPermissionJob?.cancel()
        updateAlarmMangersJob?.cancel()
        timeTicker.onDestroy()
        requestPermissionLauncher.unregister()
    }

}