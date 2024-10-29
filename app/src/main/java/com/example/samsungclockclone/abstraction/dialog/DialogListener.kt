package com.example.samsungclockclone.abstraction.dialog

import kotlinx.coroutines.flow.Flow

interface DialogListener {

    suspend fun changedVisibilityPermissionPostNotificationDialog(isVisible: Boolean)
    fun collectVisibilityDisplayPermissionPostNotificationDialog(): Flow<Boolean>

    suspend fun changedVisibilityShortInfoDialog(isVisible: Boolean)
    fun collectVisibilityDisplayShortInfoDialog(): Flow<Boolean>

}