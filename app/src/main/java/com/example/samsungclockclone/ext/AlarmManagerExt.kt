package com.example.samsungclockclone.ext

import android.app.AlarmManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun AlarmManager.checkPermission(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val result = canScheduleExactAlarms()
        if (result) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    } else {
        onPermissionGranted()
    }
}

fun AlarmManager.suspendCheckPermission(
    coroutineScope: CoroutineScope,
    onPermissionGranted: suspend () -> Unit,
    onPermissionDenied: suspend () -> Unit
) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val result = canScheduleExactAlarms()
        if (result) {
            coroutineScope.launch {
                if (!isActive) return@launch
                onPermissionGranted()
            }
        } else {
            coroutineScope.launch {
                if (!isActive) return@launch
                onPermissionDenied()
            }
        }
    } else {
        coroutineScope.launch {
            if (!isActive) return@launch
            onPermissionGranted()
        }
    }
}